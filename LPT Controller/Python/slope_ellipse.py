# -*- coding: utf-8 -*-
"""
Created on Thu Jan 12 12:02:43 2017

@author: Lor
"""
from __future__ import division
import numpy as np
import matplotlib.pyplot as plt
import sys
from scipy import integrate, linalg

#funzione che calcola il fit con l'ellisse che verra definita con yfit

def fit_ellipseFitzgibbon(x,y):
    D=np.zeros((x.size,6));
    D[:,0]=x*x;
    D[:,1]=x*y;
    D[:,2]=y*y;
    D[:,3]=x;
    D[:,4]=y;
    D[:,5]=1;
    
    S=np.matmul(D.transpose(),D);
	
    C=np.zeros((6,6));
    C[5,5]=0;
    C[0,2]=2;
    C[1,1]=-1;
    C[2,0]=2;

    invS=linalg.inv(S)
    (eigval,eigvec) = linalg.eig(np.matmul(invS,C),check_finite=True,right=True);
       
    realEigval=np.real(eigval);
  
    condition=(realEigval>0)&(np.isfinite(realEigval))

    p=np.argwhere(condition)
    
    return eigvec.transpose()[p]


########################################################
#
# INPUT
#
########################################################

filename = sys.argv[1]
x0       = float(sys.argv[2])
y0       = float(sys.argv[3])
f        = float(sys.argv[4])#mm
do_plot  = int(sys.argv[5])
file_out = sys.argv[6]

########################################################


file00 = open(filename, 'r')
slopes = file00.readlines()[10:]

gamma = np.pi/2 + 0# angolo di inclinazione ccd
x = []
l = []
y = []
corr = []
for line in (slopes):
    columns = line.split()
    x.append(float(columns[0]))
    y.append(float(columns[1]))
    l.append(float(columns[2]))
file00.close()

x = np.array(x)
y = np.array(y)
l = np.array(l)

b=(l-x0)/1000

corr = ((x-max(x)/2)**2*(-2.36e-9))
corr =0.# corr+(max(corr)-min(corr))/2

z = np.double(0.5*np.arctan(b/f))-corr
h = np.zeros(len(z))

coefs = np.polyfit(x, z, 1)
p = np.poly1d(coefs)

yy=integrate.cumtrapz(z,x,initial=0);
    
# Subtract line
line = (yy[yy.size-1]-y[0])/(x[x.size-1]-x[0])*(x-x[0])+yy[0];
yy=yy-line;
    
a = fit_ellipseFitzgibbon(x, yy).flatten()

y_fit = np.zeros(x.size)
        
for i in range(0,x.size):
    aa=a[2];
    bb=a[1]*x[i] + a[4];
    cc=(a[0]*x[i]+ a[3])*x[i] + a[5]
    y_fit[i]=(-bb+np.sqrt(bb**2-4*cc*aa))/(2.0*aa);

# NOT WORKING!!!!!
#y_fit = y_fit.diff(x)

################
# TODO
#
a_ellipse = 130000 # mm
b_ellipse = 630 # mm
################

tilt = np.average(z)*1000000
slope_error = (z - y_fit)*1000000
RMS = np.sqrt((np.sum(slope_error**2.))/len(slope_error))
Radius = 1/coefs[0]

dx = x[1] - x[0]
for i in range(len(h)):
	h[i] = dx*sum(1/1000000*slope_error[0:i])

RMSH = np.sqrt((np.sum(h**2.))/len(h))*1e6

print('RMS(urad) = %0.4f'%RMS)
print('RMSH(nm) = %0.4f'%RMSH)
print('Radius(mm) = %0.4f'%Radius)

with open(file_out, 'w') as f:
	f.write("HEADER=6\n")
	f.write("POINTS=" + str(len(x)) + "\n")
	f.write("RMS_SLOPE=" + str(RMS) + "\n")
	f.write("RMS_H=" + str(RMSH) + "\n")
	f.write("TILT=" + str(tilt) + "\n")
	f.write("RADIUS_OF_CURVATURE=" + str(Radius) + "\n")
	f.write("A=" + str(a_ellipse) + "\n")
	f.write("B=" + str(b_ellipse) + "\n")

	for index in range(len(x)):
		f.write(str(x[index]) + "," + str(slope_error[index]) + "," + str(h[index]) + "\n")

if do_plot==1:
	font ={'family': 'serif'
		   ,'color': 'darkred',
		   'weight': 'normal',
		   'size': 16,
		   }

	f, (ax1, ax2) = plt.subplots(2, 1, sharex=True)
	f.set_figwidth(9)
	f.set_figheight(9)
	
	mngr = plt.get_current_fig_manager()
	mngr.window.wm_geometry("+100+50")

	ax1.plot(x, slope_error, linewidth=2.0)
	ax1.set_xlabel('Mirror scan (mm)')
	ax1.set_ylabel('Slope error (urad)')
	ax1.text(0,2,'RMS=', fontdict=font)
	ax1.text(50,2,'{0:.4f}'.format(RMS), fontdict=font)
	ax1.text(130,-2.,'Radius=', fontdict=font)
	ax1.text(180,-2.,'{0:.0f}'.format(Radius), fontdict=font)

	## Figura confronto 	
	ax2.plot(x, h, '.')
	ax2.set_xlabel('Mirror scan (mm)')
	ax2.set_ylabel('High (mm)')

	plt.show()

