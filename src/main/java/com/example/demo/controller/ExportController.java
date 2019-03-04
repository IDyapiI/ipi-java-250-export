package com.example.demo.controller;

import com.example.demo.entity.Client;
import com.example.demo.entity.Facture;
import com.example.demo.entity.LigneFacture;
import com.example.demo.service.ClientService;
import com.example.demo.service.FactureService;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

/**
 * Controlleur pour réaliser les exports.
 */
@Controller
@RequestMapping("/")
public class ExportController {

    @Autowired
    private ClientService clientService;

    @Autowired
    private FactureService factureService;

    @GetMapping("/clients/csv")
    public void clientsCSV(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"clients.csv\"");
        PrintWriter writer = response.getWriter();

        List<Client> listClient = clientService.findAllClients();

        writer.println("id;nom;prenom;date de naissance; age");
        for (Client client : listClient) {
            writer.println(client.getId() + ";" + client.getNom() + ";" + client.getPrenom() + ";"
                    + client.getDateNaissance().toString() + ";" + Integer.toString(client.getAge()));
        }
    }

    @GetMapping("/factures/xlsx")
    public void clientsXLSX(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=\"fichierXlsx.xlsx\"");

        List<Client> listClient = clientService.findAllClients();
        Workbook workbook = new XSSFWorkbook();

        for (Client client : listClient) {
            Sheet sheetClient = workbook.createSheet(client.getNom());
            Set<Facture> listFacture = client.getFactures();
            int x= 1;
            for (Facture facture : listFacture) {
                Sheet sheet = workbook.createSheet("Facture de " + client.getNom()+ x );
                // Sheet sheet = workbook.createSheet("Facture de " + client.getNom() + facture.getId());

                Set<LigneFacture> lignes = facture.getLigne();
                int i = 0;
                double total = 0;

                Row headerRow = sheet.createRow(0);

                Cell cellId = headerRow.createCell(0);
                Cell cellNom = headerRow.createCell(1);
                Cell cellQuantite = headerRow.createCell(2);
                Cell cellPrixU = headerRow.createCell(3);
                Cell cellPrix = headerRow.createCell(4);

                cellId.setCellValue("Id");
                cellNom.setCellValue("Désignation");
                cellQuantite.setCellValue("Quantité");
                cellPrixU.setCellValue("Prix Unitaire");
                cellPrix.setCellValue("Prix Ligne");

                for (LigneFacture ligne : lignes) {
                    i++;
                    Row row = sheet.createRow(i);

                    Cell cId = row.createCell(0);
                    cId.setCellValue(ligne.getId());

                    Cell cNom = row.createCell(1);
                    cNom.setCellValue(ligne.getArticle().getLibelle());

                    Cell cQuantite = row.createCell(2);
                    cQuantite.setCellValue(ligne.getQuantite());

                    Cell cPrixU = row.createCell(3);
                    cPrixU.setCellValue(ligne.getArticle().getPrix());

                    Cell cPrix = row.createCell(4);
                    cPrix.setCellValue(ligne.getArticle().getPrix() * ligne.getQuantite());

                    total += (ligne.getArticle().getPrix() * ligne.getQuantite());
                }
                Row rowFinal = sheet.createRow(i + 2);
                Cell cellTitre = rowFinal.createCell(3);
                cellTitre.setCellValue("Total :");
                Cell cellTotal = rowFinal.createCell(4);
                cellTotal.setCellValue(total);
                CellStyle style = workbook.createCellStyle();
                Font font = workbook.createFont();
                font.setColor(IndexedColors.RED.getIndex());
                font.setBold(true);
                style.setFont(font);

                style.setFillForegroundColor(IndexedColors.BLUE.getIndex());
                style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                style.setBorderBottom(BorderStyle.THICK);
                style.setBottomBorderColor(IndexedColors.YELLOW.getIndex());
                style.setBorderLeft(BorderStyle.THICK);
                style.setLeftBorderColor(IndexedColors.YELLOW.getIndex());
                style.setBorderRight(BorderStyle.THICK);
                style.setRightBorderColor(IndexedColors.YELLOW.getIndex());
                style.setBorderTop(BorderStyle.THICK);
                style.setTopBorderColor(IndexedColors.YELLOW.getIndex());
                cellTotal.setCellStyle(style);
                x++;
            }
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }

    @GetMapping("/clients/xlsx")
    public void factureXLSX(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=\"factureXlsx.xlsx\"");

        List<Client> listClient = clientService.findAllClients();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Clients");
        Row headerRow = sheet.createRow(0);
        Cell cellId = headerRow.createCell(0);
        Cell cellNom = headerRow.createCell(1);
        Cell cellPrenom = headerRow.createCell(2);
        Cell cellDate = headerRow.createCell(3);
        Cell cellAge = headerRow.createCell(4);
        cellId.setCellValue("Id");
        cellNom.setCellValue("Nom");
        cellPrenom.setCellValue("Prénom");
        cellDate.setCellValue("Date de naissance");
        cellAge.setCellValue("Age");

        for (int i = 0; i < listClient.size(); i++) {
            Row ligne = sheet.createRow(i + 1);

            Cell cId = ligne.createCell(0);
            cId.setCellValue(listClient.get(i).getId());

            Cell cNom = ligne.createCell(1);
            cNom.setCellValue(listClient.get(i).getNom());

            Cell cPrenom = ligne.createCell(2);
            cPrenom.setCellValue(listClient.get(i).getPrenom());

            Cell cDate = ligne.createCell(3);
            cDate.setCellValue(listClient.get(i).getDateNaissance().toString());

            Cell cAge = ligne.createCell(4);
            cAge.setCellValue(Integer.toString(listClient.get(i).getAge()));
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }
}
