package com.example.demo.service;

import com.azure.ai.documentintelligence.DocumentIntelligenceClient;
import com.azure.ai.documentintelligence.DocumentIntelligenceClientBuilder;
import com.azure.ai.documentintelligence.models.*;
import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.util.BinaryData;
import com.azure.core.util.polling.SyncPoller;
import com.example.demo.model.Output;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class FormRecognizerService {

    // set `<your-endpoint>` and `<your-key>` variables with the values from the Azure portal
    private static final String endpoint = "";
    private static final String key = "";

    DocumentIntelligenceClient documentIntelligenceClient = new DocumentIntelligenceClientBuilder()
            .credential(new AzureKeyCredential(key))
            .endpoint(endpoint)
            .buildClient();

    public List<Output> extractData(File file) throws IOException {
        File layoutDocument = file;
        Path filePath = layoutDocument.toPath();
        BinaryData layoutDocumentData = BinaryData.fromFile(filePath, (int) layoutDocument.length());

        SyncPoller<AnalyzeResultOperation, AnalyzeResultOperation> analyzeLayoutResultPoller =
                documentIntelligenceClient.beginAnalyzeDocument("medical_report",
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        new AnalyzeDocumentRequest().setBase64Source(Files.readAllBytes(layoutDocument.toPath())));

        AnalyzeResult analyzeResult = analyzeLayoutResultPoller.getFinalResult().getAnalyzeResult();
        List<Output> results = new ArrayList<>();
        for (int i = 0; i < analyzeResult.getDocuments().size(); i++) {
            final Document analyzedDocument = analyzeResult.getDocuments().get(i);
            System.out.printf("----------- Analyzing custom document %d -----------%n", i);
            System.out.printf("Analyzed document has doc type %s with confidence : %.2f%n",
                    analyzedDocument.getDocType(), analyzedDocument.getConfidence());
            analyzedDocument.getFields().forEach((key, documentField) -> {
                Output output = new Output();
                output.setKey(key);
                output.setValue(documentField.getContent());
                output.setConfidence(documentField.getConfidence());
                results.add(output);
            });
        }
        return results;

    }
}