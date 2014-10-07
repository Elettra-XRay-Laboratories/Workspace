package com.elettra.common.fit;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.optimization.DifferentiableMultivariateVectorialOptimizer;
import org.apache.commons.math.optimization.OptimizationException;
import org.apache.commons.math.optimization.fitting.ParametricRealFunction;

@SuppressWarnings("deprecation")
class PseudoVoigtFullFitter extends PseudoVoigtFitter
{
	public PseudoVoigtFullFitter(DifferentiableMultivariateVectorialOptimizer optimizer)
	{
		super(optimizer);
	}

	public PseudoVoigtFitResult fit(PseudoVoigtFitParameters parameters) throws OptimizationException, FunctionEvaluationException
	{
		return new PseudoVoigtFitResult(super.fit(new PseudoVoigtFunction(), parameters.getParameters()));
	}

	class PseudoVoigtFunction implements ParametricRealFunction
	{
		public double[] gradient(double x, double[] doubles) throws FunctionEvaluationException
		{
			double[] gradient = new double[5];
			try
			{
				PseudoVoigtFitParameters parameters = new PseudoVoigtFitParameters(doubles);

				gradient[0] = this.dPV_dAmplitude(x, parameters);
				gradient[1] = this.dPV_dCenter(x, parameters);
				gradient[2] = this.dPV_dSigma(x, parameters);
				gradient[3] = this.dPV_dShape(x, parameters);
				gradient[4] = this.dPV_dOffset(x, parameters);
			}
			catch (ArrayIndexOutOfBoundsException e)
			{
				throw new FunctionEvaluationException(e, doubles);
			}

			return gradient;
		}

		public double value(double x, double[] doubles) throws FunctionEvaluationException
		{
			PseudoVoigtFitParameters parameters = new PseudoVoigtFitParameters(doubles);

			return (parameters.getOffset() + (parameters.getAmplitude() * PseudoVoigtFitter.pseudovoigt(x, parameters)));
		}

		private double dPV_dAmplitude(double x, PseudoVoigtFitParameters parameters)
		{
			return PseudoVoigtFitter.pseudovoigt(x, parameters);
		}

		private double dPV_dCenter(double x, PseudoVoigtFitParameters parameters)
		{
			return parameters.getAmplitude() * (parameters.getShape() * dlorentzian_dCenter(x, parameters) + (1 - parameters.getShape()) * dgaussian_dCenter(x, parameters));
		}

		private double dlorentzian_dCenter(double x, PseudoVoigtFitParameters parameters)
		{
			return Math.pow(PseudoVoigtFitter.pv_lorentzian(x, parameters), 2) * (2 * parameters.getCenter() / parameters.getSigma()) * ((x - parameters.getCenter()) / parameters.getSigma());
		}

		private double dgaussian_dCenter(double x, PseudoVoigtFitParameters parameters)
		{
			return PseudoVoigtFitter.pv_gaussian(x, parameters) * ((2 * Math.log(2)) / parameters.getSigma()) * ((x - parameters.getCenter()) / parameters.getSigma());
		}

		private double dPV_dSigma(double x, PseudoVoigtFitParameters parameters)
		{
			return parameters.getAmplitude() * (parameters.getShape() * dlorentzian_dSigma(x, parameters) + (1 - parameters.getShape()) * dgaussian_dSigma(x, parameters));
		}

		private double dlorentzian_dSigma(double x, PseudoVoigtFitParameters parameters)
		{
			return Math.pow(PseudoVoigtFitter.pv_lorentzian(x, parameters), 2) * (2 / parameters.getSigma()) * Math.pow(((x - parameters.getCenter()) / parameters.getSigma()), 2);
		}

		private double dgaussian_dSigma(double x, PseudoVoigtFitParameters parameters)
		{
			return PseudoVoigtFitter.pv_gaussian(x, parameters) * ((2 * Math.log(2)) / parameters.getSigma()) * Math.pow(((x - parameters.getCenter()) / parameters.getSigma()), 2);
		}

		private double dPV_dShape(double x, PseudoVoigtFitParameters parameters)
		{
			return parameters.getAmplitude() * (PseudoVoigtFitter.pv_lorentzian(x, parameters) - PseudoVoigtFitter.pv_gaussian(x, parameters));
		}

		private double dPV_dOffset(double x, PseudoVoigtFitParameters parameters)
		{
			return 1d;
		}
	}
}