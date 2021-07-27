package org.dddjava.jig.domain.model.models.jigobject.member;

import org.dddjava.jig.domain.model.parts.classes.field.FieldDeclarations;
import org.dddjava.jig.domain.model.parts.classes.type.TypeIdentifiers;

import java.util.List;
import java.util.stream.Collectors;

public class JigFields {
    List<JigField> list;

    public JigFields(List<JigField> list) {
        this.list = list;
    }

    public boolean empty() {
        return list.isEmpty();
    }

    public FieldDeclarations fieldDeclarations() {
        return new FieldDeclarations(list.stream().map(jigField -> jigField.fieldDeclaration).collect(Collectors.toList()));
    }

    public TypeIdentifiers typeIdentifies() {
        return list.stream().map(jigField -> jigField.fieldDeclaration.typeIdentifier()).collect(TypeIdentifiers.collector());
    }

    public List<JigField> list() {
        return list;
    }
}
