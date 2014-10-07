package com.elettra.common.fit;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.optimization.OptimizationException;
import org.apache.commons.math.optimization.fitting.GaussianFitter;
import org.apache.commons.math.optimization.fitting.GaussianFunction;
import org.apache.commons.math.optimization.general.AbstractLeastSquaresOptimizer;
import org.apache.commons.math.optimization.general.GaussNewtonOptimizer;
import org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizer;
import org.apache.commons.math.stat.StatUtils;
import org.apache.commons.math.stat.regression.SimpleRegression;
import org.jfree.data.xy.XYSeries;

import com.elettra.common.utilities.ObjectUtilities;

@SuppressWarnings("deprecation")
public final class FitUtilities
{
	public static final double GAUSSIAN_FWHM_TO_SIGMA = 1 / (2 * Math.sqrt(2 * Math.log(2)));
	public static final double PV_FWHM_TO_SIGMA       = 0.5;

	public class Optimizers
	{
		public static final int GAUSS_NEWTON_LU_DECOMPOSITION = 0;
		public static final int GAUSS_NEWTON_QR_DECOMPOSITION = 1;
		public static final int LEVENBERG_MARQUARDT           = 2;
	}

	public static LinearFitResult executeLinearFit(double from, double to, XYSeries series)
	{
		SimpleRegression regression = new SimpleRegression();

		for (int index = 0; index < series.getItemCount(); index++)
			if (series.getX(index).doubleValue() >= from && series.getX(index).doubleValue() <= to)
				regression.addData(series.getX(index).doubleValue(), series.getY(index).doubleValue());

		return new LinearFitResult(regression.getIntercept(), regression.getInterceptStdErr(), regression.getSlope(), regression.getSlopeStdErr(), regression.getRSquare());
	}

	public static GaussianFitResult executeGaussianFit(double from, double to, int optimizer, XYSeries series) throws OptimizationException, FunctionEvaluationException
	{
		AbstractLeastSquaresOptimizer leastSquareOptimizer = null;

		switch (optimizer)
		{
			case Optimizers.GAUSS_NEWTON_LU_DECOMPOSITION:
				leastSquareOptimizer = new GaussNewtonOptimizer(true);
			case Optimizers.GAUSS_NEWTON_QR_DECOMPOSITION:
				leastSquareOptimizer = new GaussNewtonOptimizer(false);
			case Optimizers.LEVENBERG_MARQUARDT:
				leastSquareOptimizer = new LevenbergMarquardtOptimizer();
		}

		ObjectUtilities.checkObject(leastSquareOptimizer, "Fit Optimizer");

		GaussianFitter fitter = new GaussianFitter(leastSquareOptimizer);

		for (int index = 0; index < series.getItemCount(); index++)
			if (series.getX(index).doubleValue() >= from && series.getX(index).doubleValue() <= to)
				fitter.addObservedPoint(series.getX(index).doubleValue(), series.getY(index).doubleValue());

		GaussianFunction fitFunction = fitter.fit();

		GaussianFitResult result = new GaussianFitResult(fitFunction.getA(), fitFunction.getB(), fitFunction.getC(), fitFunction.getD());
		result.setChi2(leastSquareOptimizer.getChiSquare());
		result.setRms(leastSquareOptimizer.getRMS());

		return result;
	}

	public static PseudoVoigtFitResult executePseudoVoigtFit(double from, double to, PseudoVoigtFitParameters parameters, int optimizer, XYSeries series, boolean fixedShape) throws OptimizationException,
	    FunctionEvaluationException
	{
		AbstractLeastSquaresOptimizer leastSquareOptimizer = null;

		switch (optimizer)
		{
			case Optimizers.GAUSS_NEWTON_LU_DECOMPOSITION:
				leastSquareOptimizer = new GaussNewtonOptimizer(true);
			case Optimizers.GAUSS_NEWTON_QR_DECOMPOSITION:
				leastSquareOptimizer = new GaussNewtonOptimizer(false);
			case Optimizers.LEVENBERG_MARQUARDT:
				leastSquareOptimizer = new LevenbergMarquardtOptimizer();
		}

		ObjectUtilities.checkObject(leastSquareOptimizer, "Fit Optimizer");

		PseudoVoigtFitter fitter = fixedShape ? new PseudoVoigtFixedShapeFitter(leastSquareOptimizer) : new PseudoVoigtFullFitter(leastSquareOptimizer);

		for (int index = 0; index < series.getItemCount(); index++)
			if (series.getX(index).doubleValue() >= from && series.getX(index).doubleValue() <= to)
				fitter.addObservedPoint(series.getX(index).doubleValue(), series.getY(index).doubleValue());

		PseudoVoigtFitResult result = fitter.fit(parameters);
		result.setChi2(leastSquareOptimizer.getChiSquare());
		result.setRms(leastSquareOptimizer.getRMS());

		return result;
	}

	public static MiddlePointResult findMiddlePoint(double plateauFrom, double plateauTo, double downFrom, double downTo, XYSeries series)
	{
		SimpleRegression regression = new SimpleRegression();

		List<Double> plateauList = new ArrayList<Double>();

		for (int index = 0; index < series.getItemCount(); index++)
		{
			if (series.getX(index).doubleValue() >= downFrom && series.getX(index).doubleValue() <= downTo)
				regression.addData(series.getX(index).doubleValue(), series.getY(index).doubleValue());

			if (series.getX(index).doubleValue() >= plateauFrom && series.getX(index).doubleValue() <= plateauTo)
				plateauList.add(series.getY(index).doubleValue());
		}

		double[] plateau = new double[plateauList.size()];

		for (int index = 0; index < plateauList.size(); index++)
			plateau[index] = plateauList.get(index);

		double middlePointSignalHeight = StatUtils.mean(plateau) / 2;
		double middlePointPosition = (middlePointSignalHeight - regression.getIntercept()) / regression.getSlope();

		MiddlePointResult result = new MiddlePointResult(regression.getIntercept(), regression.getInterceptStdErr(), regression.getSlope(), regression.getSlopeStdErr(), regression.getRSquare());
		result.setMiddlePointPosition(middlePointPosition);
		result.setMiddlePointSignalHeight(middlePointSignalHeight);

		return result;
	}

	public static double getFittedLinearFunctionValue(double x, LinearFitResult result)
	{
		return result.getIntercept() + result.getSlope() * x;
	}

	public static double getFittedGaussianFunctionValue(double x, GaussianFitResult result)
	{
		return result.getShift() + result.getHeight() * Math.exp((-Math.pow((x - result.getPosition()), 2) / (2 * Math.pow(result.getSigma(), 2))));
	}

	public static double getFittedPseudoVoigtFunctionValue(double x, PseudoVoigtFitResult result)
	{
		return result.getOffset() + (result.getAmplitude() * PseudoVoigtFitter.pseudovoigt(x, new PseudoVoigtFitParameters(result.getParameters())));
	}
}
