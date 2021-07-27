package org.dddjava.jig.domain.model.documents.diagrams;

import org.dddjava.jig.domain.model.documents.documentformat.DocumentName;
import org.dddjava.jig.domain.model.documents.documentformat.JigDocument;
import org.dddjava.jig.domain.model.documents.stationery.DiagramSource;
import org.dddjava.jig.domain.model.documents.stationery.DiagramSourceWriter;
import org.dddjava.jig.domain.model.documents.stationery.DiagramSources;
import org.dddjava.jig.domain.model.documents.stationery.JigDocumentContext;
import org.dddjava.jig.domain.model.models.applications.ServiceAngles;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * ユースケース複合図
 */
public class CompositeUsecaseDiagram implements DiagramSourceWriter {

    private final List<CompositeUsecases> list;

    public CompositeUsecaseDiagram(ServiceAngles serviceAngles) {
        this.list = serviceAngles.list().stream()
                .map(CompositeUsecases::new)
                .collect(Collectors.toList());
    }

    public DiagramSources sources(JigDocumentContext jigDocumentContext) {
        if (list.isEmpty()) {
            return DiagramSources.empty();
        }
        List<DiagramSource> diagramList = new ArrayList<>();

        DocumentName documentName = DocumentName.of(JigDocument.CompositeUsecaseDiagram);
        String text = list.stream()
                .map(compositeUsecases -> compositeUsecases.dotText(jigDocumentContext))
                .collect(Collectors.joining("\n", "digraph \"" + documentName.label() + "\" {\n" +
                        "layout=fdp;\n" +
                        "label=\"" + documentName.label() + "\";\n" +
                        "node[shape=box];\n" +
                        "edge[arrowhead=none];\n" +
                        "", "}"));
        DiagramSource compositeUsecaseDiagram = DiagramSource.createDiagramSourceUnit(documentName, text);
        diagramList.add(compositeUsecaseDiagram);

        boolean containsHandler = false;
        StringJoiner handlersText = new StringJoiner("\n", "digraph \"" + documentName.label() + "\" {\n" +
                "layout=fdp;\n" +
                "label=\"" + documentName.label() + "\";\n" +
                "node[shape=box];\n" +
                "edge[arrowhead=none];\n" +
                "", "}");
        for (CompositeUsecases compositeUsecases : list) {
            if (compositeUsecases.usecase.isHandler()) {
                String handlerText = compositeUsecases.dotText(jigDocumentContext);
                handlersText.add(handlerText);
                containsHandler = true;
            }
        }
        if (containsHandler) {
            DiagramSource handlersDiagramSource = DiagramSource.createDiagramSourceUnit(
                    documentName.withSuffix("-handler"), handlersText.toString());
            diagramList.add(handlersDiagramSource);
        }

        return DiagramSource.createDiagramSource(diagramList);
    }
}
