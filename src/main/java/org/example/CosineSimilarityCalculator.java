    package org.example;

    import org.apache.commons.math3.linear.ArrayRealVector;
    import org.apache.commons.math3.linear.RealVector;
    import org.json.JSONArray;

    public class CosineSimilarityCalculator {

        public static double cosineSimilarity(JSONArray vector1, JSONArray vector2) {
            // Предполагается, что оба вектора одинаковой длины
            int dimension = vector1.length();

            RealVector v1 = new ArrayRealVector(dimension);
            RealVector v2 = new ArrayRealVector(dimension);

            // Заполняем векторы значениями из JSONArray
            for (int i = 0; i < dimension; i++) {
                v1.setEntry(i, vector1.getDouble(i));
                v2.setEntry(i, vector2.getDouble(i));
            }

            // Вычисляем косинусную близость с помощью скалярного произведения
            return v1.dotProduct(v2);
        }
    }
