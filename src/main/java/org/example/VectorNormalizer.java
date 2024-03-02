package org.example;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class VectorNormalizer {
    public static JSONArray normalize(JSONArray vector) {
        double[] normalizedVector = normalizeVector(getDoubleArray(vector));
        return convertToJsonArray(normalizedVector);
    }

    private static double[] normalizeVector(double[] vector) {
        double[] normalizedVector = new double[vector.length];
        double sumOfSquares = 0.0;
        for (double v : vector) {
            sumOfSquares += Math.pow(v, 2);
        }
        double length = Math.sqrt(sumOfSquares);
        for (int i = 0; i < vector.length; i++) {
            normalizedVector[i] = vector[i] / length;
        }
        return normalizedVector;
    }

    private static double[] getDoubleArray(JSONArray jsonArray) {
        double[] array = new double[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            array[i] = jsonArray.getDouble(i);
        }
        return array;
    }

    private static JSONArray convertToJsonArray(double[] array) {
        List<Double> list = new ArrayList<>();
        for (double d : array) {
            list.add(d);
        }
        return new JSONArray(list);
    }
}
