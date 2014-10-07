package com.elettra.common.fit;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.optimization.DifferentiableMultivariateVectorialOptimizer;
import org.apache.commons.math.optimization.OptimizationException;
import org.apache.commons.math.optimization.fitting.CurveFitter;

@SuppressWarnings("deprecation")
public abstract class PseudoVoigtFitter extends CurveFitter
{

	public PseudoVoigtFitter(DifferentiableMultivariateVectorialOptimizer optimizer)
	{
		super(optimizer);

	}

	public abstract PseudoVoigtFitResult fit(PseudoVoigtFitParameters parameters) throws OptimizationException, FunctionEvaluationException;

	// ----------------------------------------------------------------------------------------------------------

	static double pv_lorentzian(double x, PseudoVoigtFitParameters parameters)
	{
		return 1 / (1 + Math.pow((x - parameters.getCenter()) / parameters.getSigma(), 2));
	}

	static double pv_gaussian(double x, PseudoVoigtFitParameters parameters)
	{
		return Math.exp(-Math.log(2) * Math.pow((x - parameters.getCenter()) / parameters.getSigma(), 2));
	}

	static double pseudovoigt(double x, PseudoVoigtFitParameters parameters)
	{
		return parameters.getShape() * PseudoVoigtFitter.pv_lorentzian(x, parameters) + (1 - parameters.getShape()) * PseudoVoigtFitter.pv_gaussian(x, parameters);
	}

}
