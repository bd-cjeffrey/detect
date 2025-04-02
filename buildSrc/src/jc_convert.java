import org.apache.poi.ss.usermodel.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class SpreadsheetToJsonConverter {
    public static void main(String[] args) {
        String excelFilePath = "example.xlsx"; // Path to your Excel file
        try (FileInputStream fis = new FileInputStream(new File(excelFilePath))) {
            Workbook workbook = WorkbookFactory.create(fis);
            Sheet sheet = workbook.getSheetAt(0); // Read the first sheet

            // Create a JSON array to hold rows
            ObjectMapper mapper = new ObjectMapper();
            ArrayNode jsonArray = mapper.createArrayNode();

            // Get the header row
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                System.out.println("The spreadsheet is empty.");
                return;
            }

            // Iterate through rows
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                // Create a JSON object for each row
                ObjectNode jsonObject = mapper.createObjectNode();
                for (int j = 0; j < headerRow.getLastCellNum(); j++) {
                    Cell headerCell = headerRow.getCell(j);
                    Cell cell = row.getCell(j);

                    if (headerCell != null && cell != null) {
                        String header = headerCell.getStringCellValue();
                        String value = getCellValueAsString(cell);
                        jsonObject.put(header, value);
                    }
                }
                jsonArray.add(jsonObject);
            }

            // Convert JSON array to string and output it
            String jsonOutput = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonArray);
            System.out.println(jsonOutput);

            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getCellValueAsString(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }
}