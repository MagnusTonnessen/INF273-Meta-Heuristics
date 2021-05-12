package utils;

import algorithms.SearchingAlgorithm;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static utils.Constants.ADAPTIVE_LARGE_NEIGHBOURHOOD_SEARCH;
import static utils.Constants.SEARCHING_ALGORITHMS;
import static utils.Utils.getInstanceName;

@SuppressWarnings("DuplicatedCode")
public class JSONCreator {

    public static void saveToJSON(Map<String, Map<String, Map<String, Object>>> map, String filePath) throws Exception {
        new ObjectMapper().writeValue(new File(filePath), map);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Map<String, Map<String, Object>>> readJSONToMap(String filePath) throws Exception {
        return new ObjectMapper().readValue(new File(filePath), Map.class);
    }

    @SuppressWarnings("unchecked")
    public static void JSONToPDF(String jsonPath, String pdfPath, String documentTitle, String algorithm, List<String> instances) throws Exception {
        PDFCreator pdf = new PDFCreator(pdfPath);
        pdf.openDocument();

        pdf.addTitle(documentTitle, 20);

        Map<String, Map<String, Map<String, Object>>> jsonAsMap = readJSONToMap(jsonPath);

        for (String filePath : instances) {

            pdf.newTable(getInstanceName(filePath));

            for (SearchingAlgorithm search : SEARCHING_ALGORITHMS) {
                try {
                    pdf.addRow(search.getName(),
                            (double) jsonAsMap.get(getInstanceName(filePath)).get(search.getName()).get("Average objective"),
                            (double) jsonAsMap.get(getInstanceName(filePath)).get(search.getName()).get("Best objective"),
                            (double) jsonAsMap.get(getInstanceName(filePath)).get(search.getName()).get("Improvement"),
                            (double) jsonAsMap.get(getInstanceName(filePath)).get(search.getName()).get("Average run time")
                    );
                } catch (Exception ignored) {
                }
            }

            pdf.addTable();
        }

        pdf.newPage();

        pdf.addTitle("Best solutions found with " + algorithm, 14);

        List<int[]> bestSolutions = jsonAsMap
                .values()
                .stream()
                .map(instance -> ((List<Integer>) instance.get(algorithm).get("Best solution")).stream().mapToInt(i -> i).toArray())
                .sorted(Comparator.comparingInt(solution -> solution.length))
                .collect(Collectors.toList());

        pdf.addBestSolutions(bestSolutions, instances);

        pdf.closeDocument();
    }

    @SuppressWarnings("unchecked")
    public static void JSONToPDFExam(String jsonPath, String pdfPath, String documentTitle, String algorithm, List<String> instances) throws Exception {
        PDFCreator pdf = new PDFCreator(pdfPath);
        pdf.openDocument();

        pdf.addTitle(documentTitle, 20);

        Map<String, Map<String, Map<String, Object>>> jsonAsMap = readJSONToMap(jsonPath);

        for (String filePath : instances) {

            pdf.newTableWithoutAvgObj(getInstanceName(filePath));

            try {
                pdf.addRowWithoutAvgObj(ADAPTIVE_LARGE_NEIGHBOURHOOD_SEARCH.getName(),
                        (double) jsonAsMap.get(getInstanceName(filePath)).get(ADAPTIVE_LARGE_NEIGHBOURHOOD_SEARCH.getName()).get("Best objective"),
                        (double) jsonAsMap.get(getInstanceName(filePath)).get(ADAPTIVE_LARGE_NEIGHBOURHOOD_SEARCH.getName()).get("Improvement"),
                        (double) jsonAsMap.get(getInstanceName(filePath)).get(ADAPTIVE_LARGE_NEIGHBOURHOOD_SEARCH.getName()).get("Average run time")
                );
            } catch (Exception ignored) {
            }

            pdf.addTable();
        }

        pdf.newPage();

        pdf.addTitle("Best solutions found with " + algorithm, 14);

        List<int[]> bestSolutions = jsonAsMap
                .values()
                .stream()
                .map(instance -> ((List<Integer>) instance.get(algorithm).get("Best solution")).stream().mapToInt(i -> i).toArray())
                .sorted(Comparator.comparingInt(solution -> solution.length))
                .collect(Collectors.toList());

        pdf.addBestSolutions(bestSolutions, instances);

        pdf.closeDocument();
    }

}
