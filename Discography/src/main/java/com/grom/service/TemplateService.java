package com.grom.service;

import com.grom.model.Man;
import com.grom.model.Group;
import org.apache.commons.io.FileUtils;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRow;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@Service
public class TemplateService {
    public File convert(MultipartFile file) {
        File result = null;
        try {
            result = new File(file.getOriginalFilename());
            FileOutputStream fos = new FileOutputStream(result);
            fos.write(file.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public XWPFDocument getMicrosoftWord(File template) throws Exception {
        File copy = new File("copy.docx");
        FileUtils.copyFile(template, copy);
        return new XWPFDocument(OPCPackage.open(copy.getName()));
    }

    public File fillMicrosoftWord(Group group, XWPFDocument document) throws Exception {
        // insert generated data
        final int[] field = {0};
        document.getParagraphs().forEach(p -> {
            p.getRuns().forEach(run -> {
                String text = run.getText(0);
                if (text != null && text.equals(" ")) {
                    switch (field[0]) {
                        case 0: run.setText(group.getCourse(), 0); break;
                        case 1: run.setText(group.getGroupNumber(), 0); break;
                        case 2: run.setText(group.getGroupLead(), 0); break;
                        case 3: run.setText(group.getYear(), 0); break;
                        default: break;
                    }
                    field[0]++;
                }
            });
        });
        System.out.println("BUG");

        XWPFHeaderFooterPolicy header = document.getHeaderFooterPolicy();

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        XWPFTable table = document.getTableArray(0);

        // insert input data
        XWPFTableRow oldRow = table.getRow(1);
        CTRow ctrow = CTRow.Factory.parse(oldRow.getCtRow().newInputStream());
        XWPFTableRow newRow = new XWPFTableRow(ctrow, table);

        for (Man man: group.getMans()) {
            List<XWPFTableCell> cells = newRow.getTableCells();
            for (int i = 0; i < cells.size(); i++) {
                XWPFTableCell cell = cells.get(i);
                for (XWPFParagraph paragraph : cell.getParagraphs()) {
                    for (XWPFRun run: paragraph.getRuns()) {
                        switch (i) {
                            case 0: run.setText(String.valueOf(man.getId()), 0); break;
                            case 1: run.setText(man.getFullName(), 0); break;
                            case 2:
                                Date date = man.getBirthDate();
                                String releaseDate = date.getYear() + "." + date.getMonth() + "." + date.getDate();
                                run.setText(releaseDate, 0);
                                break;
                            case 3: run.setText(String.valueOf(man.getAverageMark()), 0); break;
                            default: break;
                        }
                    }
                }
            }

            table.addRow(newRow);
            XWPFTableRow lastRow = table.getRows().get(table.getNumberOfRows() - 1);
            ctrow = CTRow.Factory.parse(lastRow.getCtRow().newInputStream());
            newRow = new XWPFTableRow(ctrow, table);
        }

        table.removeRow(1);

        File result = new File("result.docx");
        document.write(new FileOutputStream("result.docx"));
        document.close();
        return result;
    }
}
