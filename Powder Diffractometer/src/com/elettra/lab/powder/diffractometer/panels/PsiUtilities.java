package com.elettra.lab.powder.diffractometer.panels;

import com.elettra.common.io.CommunicationPortException;
import com.elettra.common.io.ICommunicationPort;
import com.elettra.common.utilities.FileIni;
import com.elettra.controller.driver.commands.CommandParameters;
import com.elettra.controller.driver.commands.CommandsFacade;
import com.elettra.controller.driver.common.ControllerPosition;
import com.elettra.controller.driver.common.DriverUtilities;
import com.elettra.controller.driver.programs.MoveParameters;
import com.elettra.controller.driver.programs.ProgramsFacade;
import com.elettra.controller.gui.common.ListenerRegister;
import com.elettra.lab.powder.diffractometer.Axis;

public class PsiUtilities
{
	public static enum SampleHolder
	{
		SPINNER, STATIC, PHI_MOTOR
	};

	private static double B0_SPINNER   = 0.0;
	private static double B0_STATIC    = 0.0;
	private static double B0_PHI_MOTOR = 0.0;
	private static double L0           = 0.0;

	static
	{
		try
		{
			B0_SPINNER = Double.parseDouble(FileIni.getInstance().getProperty("B0_SPINNER"));
			B0_STATIC = Double.parseDouble(FileIni.getInstance().getProperty("B0_STATIC"));
			B0_PHI_MOTOR = Double.parseDouble(FileIni.getInstance().getProperty("B0_PHI_MOTOR"));
			L0 = Double.parseDouble(FileIni.getInstance().getProperty("L0"));
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public static synchronized PsiMoveParameters calculatePsi(SampleHolder sampleHolder, ControllerPosition psiPosition, ControllerPosition sampleZPosition)
	{
		double D2 = L0;
		double deltaZ = -sampleZPosition.getSignedPosition();

		if (sampleHolder.equals(SampleHolder.SPINNER))
		{
			D2 -= B0_SPINNER;
			deltaZ += B0_SPINNER;
		}
		else if (sampleHolder.equals(SampleHolder.STATIC))
		{
			D2 -= B0_STATIC;
			deltaZ += B0_STATIC;
		}
		else if (sampleHolder.equals(SampleHolder.PHI_MOTOR))
		{
			D2 -= B0_PHI_MOTOR;
			deltaZ += B0_PHI_MOTOR;
		}

		double psi = Math.toRadians(psiPosition.getSignedPosition());

		double beta = Math.asin((deltaZ / D2) * Math.sin(psi));
		double alpha = psi + beta;

		double alphaOut = Math.toDegrees(alpha);
		double betaOut = (beta != 0 ? -1 : 1) * Math.toDegrees(beta);
		double zOut = sampleZPosition.getSignedPosition() + ((D2 * (1 - Math.cos(beta))) + (deltaZ * (1 - Math.cos(psi))));

		return new PsiMoveParameters(null, null, null, new ControllerPosition(zOut), new ControllerPosition(alphaOut), new ControllerPosition(betaOut));
	}

	public static synchronized void movePsi(PsiMoveParameters psiMoveParameters, ICommunicationPort port) throws CommunicationPortException
	{
		MoveParameters moveParameters1 = null;
		MoveParameters moveParameters2 = null;

		if (psiMoveParameters.getAxisBetaPosition().getSignedPosition() > 0 && psiMoveParameters.getAxisBetaPosition().getSignedPosition() >= psiMoveParameters.getAxisBetaPositionOut().getSignedPosition())
		{
			moveParameters1 = new MoveParameters(Axis.BETA, ListenerRegister.getInstance());
			moveParameters1.setKindOfMovement(DriverUtilities.getAbsolute());
			moveParameters1.setSign(psiMoveParameters.getAxisBetaPositionOut().getSign());
			moveParameters1.setPosition(psiMoveParameters.getAxisBetaPositionOut().getAbsolutePosition());

			moveParameters2 = new MoveParameters(Axis.ALPHA, ListenerRegister.getInstance());
			moveParameters2.setKindOfMovement(DriverUtilities.getAbsolute());
			moveParameters2.setSign(psiMoveParameters.getAxisAlphaPositionOut().getSign());
			moveParameters2.setPosition(psiMoveParameters.getAxisAlphaPositionOut().getAbsolutePosition());
		}
		else
		{
			moveParameters1 = new MoveParameters(Axis.ALPHA, ListenerRegister.getInstance());
			moveParameters1.setKindOfMovement(DriverUtilities.getAbsolute());
			moveParameters1.setSign(psiMoveParameters.getAxisAlphaPositionOut().getSign());
			moveParameters1.setPosition(psiMoveParameters.getAxisAlphaPositionOut().getAbsolutePosition());

			moveParameters2 = new MoveParameters(Axis.BETA, ListenerRegister.getInstance());
			moveParameters2.setKindOfMovement(DriverUtilities.getAbsolute());
			moveParameters2.setSign(psiMoveParameters.getAxisBetaPositionOut().getSign());
			moveParameters2.setPosition(psiMoveParameters.getAxisBetaPositionOut().getAbsolutePosition());
		}

		MoveParameters moveParameters3 = new MoveParameters(Axis.Z, ListenerRegister.getInstance());
		moveParameters3.setKindOfMovement(DriverUtilities.getAbsolute());
		moveParameters3.setSign(psiMoveParameters.getAxisZPositionOut().getSign());
		moveParameters3.setPosition(psiMoveParameters.getAxisZPositionOut().getAbsolutePosition());

		ProgramsFacade.executeProgram(ProgramsFacade.Programs.MOVE, moveParameters1, port);
		CommandsFacade.waitForTheEndOfMovement(new CommandParameters(moveParameters1.getAxis(), moveParameters1.getListener()), port);

		ProgramsFacade.executeProgram(ProgramsFacade.Programs.MOVE, moveParameters2, port);
		CommandsFacade.waitForTheEndOfMovement(new CommandParameters(moveParameters2.getAxis(), moveParameters2.getListener()), port);

		ProgramsFacade.executeProgram(ProgramsFacade.Programs.MOVE, moveParameters3, port);
		CommandsFacade.waitForTheEndOfMovement(new CommandParameters(moveParameters3.getAxis(), moveParameters3.getListener()), port);
	}
}
