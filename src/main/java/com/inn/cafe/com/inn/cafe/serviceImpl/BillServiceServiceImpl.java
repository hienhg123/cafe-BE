package com.inn.cafe.com.inn.cafe.serviceImpl;

import com.inn.cafe.com.inn.cafe.JWT.JwtAuthFilter;
import com.inn.cafe.com.inn.cafe.constants.CafeConstants;
import com.inn.cafe.com.inn.cafe.dao.BillDAO;
import com.inn.cafe.com.inn.cafe.model.Bill;
import com.inn.cafe.com.inn.cafe.service.BillService;
import com.inn.cafe.com.inn.cafe.utils.CafeUtils;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.io.IOUtils;
import org.json.JSONArray;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.itextpdf.text.FontFactory.getFont;

@Service
@Slf4j
@AllArgsConstructor
public class BillServiceServiceImpl implements BillService {

    private final JwtAuthFilter jwtAuthFilter;

    private final BillDAO billDAO;

    @Override
    public ResponseEntity<String> generateReport(Map<String, Object> requestMap) {
        log.info("Inside");
        try {
            String fileName;
            //check input data
            if (validateRequestMap(requestMap)) {
                //check if generated or not
                if (requestMap.containsKey("isGenerated") && !Boolean.parseBoolean((String) requestMap.get("isGenerated"))) {
                    fileName = (String) requestMap.get("uuid");
                } else {
                    fileName = CafeUtils.getUUID();
                    requestMap.put("uuid", fileName);
                    insertBill(requestMap);
                }
                //make data to generate pdf
                String data = "Name: " + requestMap.get("name") + "\n" + "Contact Number: " + requestMap.get("contactNumber")
                        + "\n" + "Email: " + requestMap.get("email") + "\n" + "Payment method: " + requestMap.get("paymentMethod");


                //create document and where to store
                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(CafeConstants.STORE_LOCATION + "\\" + fileName + ".pdf"));

                document.open();
                setRectangleInPdf(document);

                Paragraph chunk = new Paragraph("Cafe Management System", createFont("Header"));
                chunk.setAlignment(Element.ALIGN_CENTER);
                document.add(chunk);

                Paragraph paragraph = new Paragraph(data + "\n \n", createFont("Data"));
                document.add(paragraph);

                //generate table
                PdfPTable table = new PdfPTable(5);
                table.setWidthPercentage(100);
                addTableHeader(table);

                //add data into table
                JSONArray jsonArray = CafeUtils.getJSONArrayFromString(String.valueOf(requestMap.get("productDetails")));
                //loop to add into table
                for (int i = 0; i < jsonArray.length(); i++) {
                    //add row to table
                    addRows(table, CafeUtils.getMapFromJson(jsonArray.getString(i)));
                }
                document.add(table);
                //add footer
                Paragraph footer = new Paragraph("Total: " + requestMap.get("total") + "\n"
                        + "Thanks for visiting");

                document.add(footer);
                document.close();
                return new ResponseEntity<>("{\"uuid\":\"" + fileName + "\"}", HttpStatus.OK);

            } else {
                return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    private void addRows(PdfPTable table, Map<String, Object> data) {
        log.info("Add row");
        table.addCell(String.valueOf(data.get("name")));
        table.addCell(String.valueOf(data.get("category")));
        table.addCell(String.valueOf(data.get("quantity")));
        table.addCell(Double.toString((Double) data.get("price")));
        table.addCell(Double.toString((Double) data.get("total")));
    }

    private void addTableHeader(PdfPTable table) {
        log.info("Inside add table header");
        Stream.of("Name", "Category", "Quantity", "Price", "Sub Total")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(columnTitle));
                    header.setBackgroundColor(BaseColor.YELLOW);
                    header.setHorizontalAlignment(Element.ALIGN_CENTER);
                    header.setVerticalAlignment(Element.ALIGN_CENTER);
                    table.addCell(header);
                });

    }

    private Font createFont(String header) {
        log.info("inside get font");
        switch (header) {
            case "Header":
                Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE, 18, BaseColor.BLACK);
                headerFont.setStyle(Font.BOLD);
                return headerFont;
            case "Data":
                Font dataFont = FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, BaseColor.BLACK);
                dataFont.setStyle(1);
                return dataFont;
            default:
                return new Font();
        }
    }

    private void setRectangleInPdf(Document document) throws DocumentException {
        log.info("Inside set rectangle pdf");
        Rectangle rectangle = new Rectangle(577, 825, 18, 15);
        rectangle.enableBorderSide(1);
        rectangle.enableBorderSide(2);
        rectangle.enableBorderSide(4);
        rectangle.enableBorderSide(8);
        rectangle.setBorderColor(BaseColor.BLACK);
        rectangle.setBorderWidth(1);
        document.add(rectangle);
    }

    private void insertBill(Map<String, Object> requestMap) {
        try {
            Bill bill = Bill.builder()
                    .uuid(String.valueOf(requestMap.get("uuid")))
                    .name(String.valueOf(requestMap.get("name")))
                    .email(String.valueOf(requestMap.get("email")))
                    .contactNumber(String.valueOf(requestMap.get("contactNumber")))
                    .paymentMethod(String.valueOf(requestMap.get("paymentMethod")))
                    .total(Integer.parseInt((String) requestMap.get("total")))
                    .productDetails(String.valueOf(requestMap.get("productDetails")))
                    .createdBy(jwtAuthFilter.getCurrentUser())
                    .build();
            billDAO.save(bill);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private boolean validateRequestMap(Map<String, Object> requestMap) {
        return requestMap.containsKey("name") && requestMap.containsKey("contactNumber")
                && requestMap.containsKey("email") && requestMap.containsKey("paymentMethod")
                && requestMap.containsKey("productDetails") && requestMap.containsKey("total");

    }

    @Override
    public ResponseEntity<List<Bill>> getAllBill() {
        List<Bill> list = new ArrayList<>();
        //check admin
        if (jwtAuthFilter.isAdmin()) {
            list = billDAO.getAllBills();
        } else {
            list = billDAO.findByCreatedByOrderById(jwtAuthFilter.getCurrentUser());
        }
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<byte[]> getPdf(Map<String, Object> requestMap) {
        log.info("inside get pdf request map {}: ", requestMap);
        try {
            byte[] bytes = new byte[0];
            if (!requestMap.containsKey("uuid") && validateRequestMap(requestMap)) {
                return new ResponseEntity<>(bytes, HttpStatus.BAD_REQUEST);
            }
            String filePath = CafeConstants.STORE_LOCATION + "\\" + String.valueOf(requestMap.get("uuid")) + ".pdf";

            //check if file is exist or not
            if (CafeUtils.isFileExist(filePath)) {
                bytes = getByteArray(filePath);
                return new ResponseEntity<>(bytes, HttpStatus.OK);
            } else {
                requestMap.put("isGenerate", false);
                generateReport(requestMap);
                bytes = getByteArray(filePath);
                return new ResponseEntity<>(bytes, HttpStatus.OK);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private byte[] getByteArray(String filePath) throws IOException {
        File initialFile = new File(filePath);
        InputStream targetStream = new FileInputStream(initialFile);
        byte[] bytes = IOUtils.toByteArray(targetStream);
        targetStream.close();
        return bytes;
    }


}
