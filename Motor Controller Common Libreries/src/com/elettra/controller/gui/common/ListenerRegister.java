package com.elettra.controller.gui.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.elettra.common.io.CommunicationPortException;
import com.elettra.common.io.ICommunicationPort;
import com.elettra.common.utilities.ObjectUtilities;
import com.elettra.controller.driver.listeners.IDriverListener;
import com.elettra.controller.driver.listeners.MeasurePoint;
import com.elettra.controller.driver.listeners.Progress;
import com.elettra.controller.gui.fit.IFitResultListener;
import com.elettra.controller.gui.fit.IMotorCoordinateListener;
import com.elettra.controller.gui.fit.MovementMatrix;
import com.elettra.controller.gui.fit.PlottableFitResult;

public class ListenerRegister implements IDriverListener, IFitResultListener, IMotorCoordinateListener
{
	private static ListenerRegister                    uniqueInstance = null;

	private HashMap<Integer, List<IDriverListener>>    register;
	private HashMap<Integer, List<IFitResultListener>> fitResultRegister;
	public HashMap<Integer, Double>                    motorCoordinateRegister;

	private ListenerRegister()
	{
		this.reset();
	}

	public static synchronized ListenerRegister getInstance()
	{
		if (uniqueInstance == null)
			uniqueInstance = new ListenerRegister();

		return uniqueInstance;
	}

	// -----------------------------------------------------------------------------------
	//
	// LISTENER REGISTER
	//
	// -----------------------------------------------------------------------------------

	public synchronized void reset()
	{
		this.register = new HashMap<Integer, List<IDriverListener>>();
		this.fitResultRegister = new HashMap<Integer, List<IFitResultListener>>();
		this.motorCoordinateRegister = new HashMap<Integer, Double>();
	}

	public synchronized void addListener(int axis, IDriverListener listener)
	{
		List<IDriverListener> partialList = this.register.get(Integer.valueOf(axis));

		if (partialList == null)
		{
			partialList = new ArrayList<IDriverListener>();
			this.register.put(Integer.valueOf(axis), partialList);
		}

		partialList.add(listener);
	}

	public synchronized void addFitResultListener(int axis, IFitResultListener listener)
	{
		List<IFitResultListener> partialList = this.fitResultRegister.get(Integer.valueOf(axis));

		if (partialList == null)
		{
			partialList = new ArrayList<IFitResultListener>();
			this.fitResultRegister.put(Integer.valueOf(axis), partialList);
		}

		partialList.add(listener);
	}

	// -----------------------------------------------------------------------------------
	//
	// DRIVER LISTENER
	//
	// -----------------------------------------------------------------------------------

	public synchronized void signalMeasure(int axis, MeasurePoint point, Progress progress, ICommunicationPort port) throws CommunicationPortException
	{
		ListIterator<IDriverListener> iterator = getPartialListIterator(axis);

		while (iterator.hasNext())
			iterator.next().signalMeasure(axis, point, progress, port);
	}

	public synchronized void signalXAxisRange(int axis, double startPosition, double stopPosition) throws CommunicationPortException
	{
		ListIterator<IDriverListener> iterator = getPartialListIterator(axis);

		while (iterator.hasNext())
			iterator.next().signalXAxisRange(axis, startPosition, stopPosition);
	}

	public synchronized void signalAxisMovement(int axis, ICommunicationPort port) throws CommunicationPortException
	{
		ListIterator<IDriverListener> iterator = getPartialListIterator(axis);

		while (iterator.hasNext())
			iterator.next().signalAxisMovement(axis, port);
	}

	public synchronized void signalScanStart()
	{
		Iterator<Integer> keysIterator = this.register.keySet().iterator();

		while (keysIterator.hasNext())
		{
			ListIterator<IDriverListener> iterator = getPartialListIterator(keysIterator.next().intValue());

			while (iterator.hasNext())
				iterator.next().signalScanStart();
		}
	}

	public synchronized void signalScanStop()
	{
		Iterator<Integer> keysIterator = this.register.keySet().iterator();

		while (keysIterator.hasNext())
		{
			ListIterator<IDriverListener> iterator = getPartialListIterator(keysIterator.next().intValue());
			while (iterator.hasNext())
				iterator.next().signalScanStop();
		}
	}

	public synchronized boolean isStopScanActivated(int axis)
	{
		ListIterator<IDriverListener> iterator = getPartialListIterator(axis);

		boolean isStopScanActivated = false;

		while (iterator.hasNext() && !isStopScanActivated)
			isStopScanActivated = iterator.next().isStopScanActivated(axis) || isStopScanActivated;

		return isStopScanActivated;
	}

	// -----------------------------------------------------------------------------------
	//
	// FIT RESULT LISTENER
	//
	// -----------------------------------------------------------------------------------

	public void signalFitResult(int axis, PlottableFitResult fitResult)
	{
		ListIterator<IFitResultListener> iterator = getFitResultPartialListIterator(axis);

		while (iterator.hasNext())
			iterator.next().signalFitResult(axis, fitResult);
	}

	public void signalInitialization(int axis, MovementMatrix matrix)
	{
		ListIterator<IFitResultListener> iterator = getFitResultPartialListIterator(axis);

		while (iterator.hasNext())
			iterator.next().signalInitialization(axis, matrix);
	}

	// -----------------------------------------------------------------------------------
	//
	// MOTOR COORDINATE LISTENER
	//
	// -----------------------------------------------------------------------------------
	
	public synchronized void signalMotorCoordinate(int axis, double position)
	{
		this.motorCoordinateRegister.put(Integer.valueOf(axis), Double.valueOf(position));
	}

	public synchronized double getMotorCoordinate(int axis)
	{
		if (this.motorCoordinateRegister.containsKey(Integer.valueOf(axis)))
			return this.motorCoordinateRegister.get(Integer.valueOf(axis));
		else
			throw new IllegalArgumentException("Data not found");
	}

	// -----------------------------------------------------------------------------------
	//
	// PRIVATE METHODS
	//
	// -----------------------------------------------------------------------------------

	private ListIterator<IDriverListener> getPartialListIterator(int axis)
	{
		List<IDriverListener> partialList = this.register.get(Integer.valueOf(axis));

		ObjectUtilities.checkObject(partialList, "partialList");

		ListIterator<IDriverListener> iterator = partialList.listIterator();

		return iterator;
	}

	private ListIterator<IFitResultListener> getFitResultPartialListIterator(int axis)
	{
		List<IFitResultListener> partialList = this.fitResultRegister.get(Integer.valueOf(axis));

		ObjectUtilities.checkObject(partialList, "partialList");

		ListIterator<IFitResultListener> iterator = partialList.listIterator();

		return iterator;
	}
}
