# -*- coding: utf-8 -*-
"""
Created on Thu Jan 12 12:02:43 2017

@author: Lor
"""
from __future__ import division
import numpy as np
import matplotlib.pyplot as plt
import sys

# JUST FOR TEST - TO BE REPLACED

filename = sys.argv[1]
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
f =float(sys.argv[4])#mm
x = np.array(x)
y = np.array(y)
l = np.array(l)

x0 = float(sys.argv[2])

b=(l-x0)/1000

corr = ((x-max(x)/2)**2*(-2.36e-9))
corr =0.# corr+(max(corr)-min(corr))/2

z = np.double(0.5*np.arctan(b/f))-corr
h = np.zeros(len(z))

coefs = np.polyfit(x, z, 1)
p = np.poly1d(coefs)
yfit = np.polyval(p,x)

tilt = np.average(z)*1000000
slope_error = (z - yfit)*1000000
RMS = np.sqrt((np.sum(slope_error**2.))/len(slope_error))
Radius = 1/coefs[0]

dx = x[1] - x[0]
for i in range(len(h)):
	h[i] = dx*sum(1/1000000*slope_error[0:i])

RMSH = np.sqrt((np.sum(h**2.))/len(h))*1e6

print('RMS(urad) = %0.4f'%RMS)
print('RMSH(nm) = %0.4f'%RMSH)
print('Radius(mm) = %0.4f'%Radius)

file_out = sys.argv[6]

with open(file_out, 'w') as f:
	f.write("HEADER=6\n")
	f.write("POINTS=" + str(len(x)) + "\n")
	f.write("RMS_SLOPE=" + str(RMS) + "\n")
	f.write("RMS_H=" + str(RMSH) + "\n")
	f.write("TILT=" + str(tilt) + "\n")
	f.write("RADIUS_OF_CURVATURE=" + str(Radius) + "\n")
	f.write("A=" + str(10.0) + "\n")
	f.write("B=" + str(20.0) + "\n")

	for index in range(len(x)):
		f.write(str(x[index]) + "," + str(slope_error[index]) + "," + str(h[index]) + "\n")

do_plot = int(sys.argv[5])

if do_plot==1:
	font ={'family': 'serif'
		   ,'color': 'darkred',
		   'weight': 'normal',
		   'size': 16,
		   }

	f, (ax1, ax2) = plt.subplots(2, 1, sharex=True)
	f.set_figwidth(3500)
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
