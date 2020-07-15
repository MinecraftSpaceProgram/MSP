package io.github.MinecraftSpaceProgram.MSP.util;

public abstract class mathsutil {

    /**
     * @return A * B
     * @throws IllegalStateException if the matrices do not have compatible sizes
     */
    public static double[][] multiplyMatrix(double[][] A, double[][] B){
        if(A[0].length != B.length){
            throw new IllegalStateException("Cannot multiply matrices of non compatible sizes. Attempted: " + A + " * " + B);
        }
        double[][] res = new double[A.length][B[0].length];
        for(int i = 0; i < A.length; i ++){
            for(int j = 0; j < B[0].length; j ++){
                res[i][j] = 0;
                for(int k = 0; k < B.length; k++){
                    res[i][j] += A[i][k] * B[k][j];
                }
            }
        }
        return res;
    }

    /**
     * Euler's method to approximate solutions to a differential equation
     * of the second degree
     *
     * @param equation the differential equation represented as a matrix
     * @param initial the initial state of the system
     * @param size the amount of points calculated
     * @param time the time between 2 points
     * @return an array containing the position, speed and acceleration at every calculated point
     */
    public static double[][] euler(int size, double time, double[] initial, double[][] equation) {
        // state[0] = x,            state[1] = y,           state[2] = z
        // state[3] = dx/dt,        state[4] = dy/dt,       state[5] = dz/dt,
        // state[6] = d^2x/dt^2,    state[7] = d^2y/dt^2,   state[8] = d^2z/dt^2,
        double[][] state = new double[9][size];

        // equation with time taken into account
        double[][] tequation = equation.clone();
        for (double[] l: tequation) {
            for (double el: l) {
                el *= time;
            }
        }

        // sets the initial state
        state[0] = initial;

        // approximates all other states
        for (int i = 1; i < size; i++) {
            state[i] = multiplyMatrix(new double[][]{state[i - 1]}, tequation)[0];
        }
        return state;
    }

}
