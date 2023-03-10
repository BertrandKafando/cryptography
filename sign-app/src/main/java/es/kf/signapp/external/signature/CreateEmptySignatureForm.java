package es.kf.signapp.external.signature;/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDSignatureField;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;
import org.springframework.stereotype.Service;

import java.io.*;

/**
 * An example of creating an AcroForm and an empty signature field from scratch.
 * 
 * An actual signature can be added by clicking on it in Adobe Reader.
 * 
 */
@Service
public  class CreateEmptySignatureForm
{
    public static final String DEST2 = "src/main/resources/res/hello_.pdf";
    private CreateEmptySignatureForm()
    {
    }


    public  void createEmptySignatureForm( String filePath ) throws IOException
    {
        // Create a new document with an empty page.
        try (PDDocument document = PDDocument.load(new File(filePath)))
        {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            // Adobe Acrobat uses Helvetica as a default font and
            // stores that under the name '/Helv' in the resources dictionary
            PDFont font = new PDType1Font(PDType1Font.HELVETICA.getCOSObject());
            PDResources resources = new PDResources();
            resources.put(COSName.HELV, font);

            // Add a new AcroForm and add that to the document
            PDAcroForm acroForm = new PDAcroForm(document);
            document.getDocumentCatalog().setAcroForm(acroForm);

            // Add and set the resources and default appearance at the form level
            acroForm.setDefaultResources(resources);

            // Acrobat sets the font size on the form level to be
            // auto sized. This can be achieved by setting the font size to 0.
            String defaultAppearanceString = "/Helv 0 Tf 0 g";
            acroForm.setDefaultAppearance(defaultAppearanceString);

            // Add a signature field
            PDSignatureField signatureField = new PDSignatureField(acroForm);
            acroForm.getFields().add(signatureField);

            // Specify the widget annotation associated with the field
            PDAnnotationWidget widget = signatureField.getWidgets().get(0);
            PDRectangle rect = new PDRectangle(50, 700, 200, 50);
            widget.setRectangle(rect);
            widget.setPage(page);
            page.getAnnotations().add(widget);

            // Add the field to the document
            acroForm.getFields().add(signatureField);

            document.save(DEST2);
        }
    }

    public byte[] EmptyForm (byte[] pdf, String reason, String location ,int pageNumb,String name) throws IOException {
        PDDocument document = PDDocument.load(pdf);
        PDPage page = document.getPage(pageNumb);

        PDFont font = new PDType1Font(PDType1Font.HELVETICA.getCOSObject());
        PDResources resources = new PDResources();
        resources.put(COSName.HELV, font);

        // Add a new AcroForm and add that to the document
        if (document.getDocumentCatalog().getAcroForm() == null) {
            PDAcroForm acroForm = new PDAcroForm(document);
            document.getDocumentCatalog().setAcroForm(acroForm);
        }
        PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();

        // Add and set the resources and default appearance at the form level
        acroForm.setDefaultResources(resources);

        // Acrobat sets the font size on the form level to be
        // auto sized. This can be achieved by setting the font size to 0.
        String defaultAppearanceString = "/Helv 0 Tf 0 g";
        acroForm.setDefaultAppearance(defaultAppearanceString);

        // Add a signature field
        PDSignatureField signatureField = new PDSignatureField(acroForm);
        signatureField.setPartialName(name);
        acroForm.getFields().add(signatureField);

        // Specify the widget annotation associated with the field
        PDAnnotationWidget widget = signatureField.getWidgets().get(0);
        PDRectangle rect = new PDRectangle(50, 700, 200, 50);
        widget.setRectangle(rect);
        widget.setPage(page);
        page.getAnnotations().add(widget);
        // Add the field to the document
        acroForm.getFields().add(signatureField);
        // add text field

          PDTextField textField = new PDTextField(acroForm);
            textField.setPartialName("Reason");
            textField.setValue(reason);

        PDAnnotationWidget widget2 = textField.getWidgets().get(0);
        PDRectangle rect2 = new PDRectangle(100, 650, 200, 50);
        widget2.setRectangle(rect2);
        widget2.setPage(page);
        page.getAnnotations().add(widget2);
        acroForm.getFields().add(textField);

        // return modify fill like byte array.
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        document.save(out);
        document.save(DEST2);
        document.close();
        return out.toByteArray();
    }
    
    public static void main(String[] args) throws IOException
    {
        // Create a new document with an empty page.
        try (PDDocument document = new PDDocument())
        {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            // Adobe Acrobat uses Helvetica as a default font and
            // stores that under the name '/Helv' in the resources dictionary
            PDFont font = new PDType1Font(PDType1Font.HELVETICA.getCOSObject());
            PDResources resources = new PDResources();
            resources.put(COSName.HELV, font);

            // Add a new AcroForm and add that to the document
            PDAcroForm acroForm = new PDAcroForm(document);
            document.getDocumentCatalog().setAcroForm(acroForm);

            // Add and set the resources and default appearance at the form level
            acroForm.setDefaultResources(resources);

            // Acrobat sets the font size on the form level to be
            // auto sized as default. This is done by setting the font size to '0'
            String defaultAppearanceString = "/Helv 0 Tf 0 g";
            acroForm.setDefaultAppearance(defaultAppearanceString);
            // --- end of general AcroForm stuff ---

            // Create empty signature field, it will get the name "Signature1"
            PDSignatureField signatureField = new PDSignatureField(acroForm);
            PDAnnotationWidget widget = signatureField.getWidgets().get(0);
            PDRectangle rect = new PDRectangle(50, 650, 200, 50);
            widget.setRectangle(rect);
            widget.setPage(page);

            // see thread from PDFBox users mailing list 17.2.2021 - 19.2.2021
            // https://mail-archives.apache.org/mod_mbox/pdfbox-users/202102.mbox/thread
            widget.setPrinted(true);

            page.getAnnotations().add(widget);

            acroForm.getFields().add(signatureField);

            document.save(DEST2);
        }
    }
}
