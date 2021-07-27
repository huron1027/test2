package org.dddjava.jig.domain.model.sources.jigfactory;

import org.dddjava.jig.domain.model.models.jigobject.member.JigMethod;
import org.dddjava.jig.domain.model.parts.classes.annotation.Annotation;
import org.dddjava.jig.domain.model.parts.classes.annotation.MethodAnnotation;
import org.dddjava.jig.domain.model.parts.classes.annotation.MethodAnnotations;
import org.dddjava.jig.domain.model.parts.classes.field.FieldDeclaration;
import org.dddjava.jig.domain.model.parts.classes.method.*;
import org.dddjava.jig.domain.model.parts.classes.type.TypeIdentifier;
import org.dddjava.jig.domain.model.parts.relation.method.MethodDepend;
import org.dddjava.jig.domain.model.parts.relation.method.MethodRelation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * メソッドの実装から読み取れること
 */
public class JigMethodBuilder {

    MethodDeclaration methodDeclaration;
    Visibility visibility;
    MethodDerivation methodDerivation;
    List<TypeIdentifier> throwsTypes;

    List<Annotation> annotations;

    List<FieldDeclaration> fieldInstructions;
    List<MethodDeclaration> methodInstructions;

    List<TypeIdentifier> classReferenceCalls;
    List<TypeIdentifier> invokeDynamicTypes;

    Set<TypeIdentifier> useTypes = new HashSet<>();

    // 制御が飛ぶ処理がある（ifやbreak）
    private int jumpInstructionNumber;
    // switchがある
    private int lookupSwitchInstructionNumber;
    // nullを参照している
    private final boolean hasReferenceNull;
    // nullによる判定がある
    boolean hasJudgeNull;

    private MethodComment methodComment;

    public JigMethodBuilder(MethodDeclaration methodDeclaration, List<TypeIdentifier> useTypes, Visibility visibility, MethodDerivation methodDerivation, List<Annotation> annotations, List<TypeIdentifier> throwsTypes, List<FieldDeclaration> fieldInstructions, List<MethodDeclaration> methodInstructions, List<TypeIdentifier> classReferenceCalls, List<TypeIdentifier> invokeDynamicTypes, int lookupSwitchInstructionNumber, int jumpInstructionNumber, boolean hasJudgeNull, boolean hasReferenceNull) {
        this.methodDeclaration = methodDeclaration;
        this.visibility = visibility;
        this.methodDerivation = methodDerivation;
        this.throwsTypes = throwsTypes;
        this.useTypes.addAll(throwsTypes);

        // TODO useTypesは曖昧なのでなくしたい
        this.useTypes.add(methodDeclaration.methodReturn().typeIdentifier());
        this.useTypes.addAll(methodDeclaration.methodSignature().arguments());
        this.useTypes.addAll(useTypes);

        this.annotations = annotations;
        annotations.forEach(annotation -> this.useTypes.add(annotation.typeIdentifier()));

        this.fieldInstructions = fieldInstructions;
        this.methodInstructions = methodInstructions;

        this.classReferenceCalls = classReferenceCalls;
        this.useTypes.addAll(classReferenceCalls);

        this.invokeDynamicTypes = invokeDynamicTypes;
        this.useTypes.addAll(invokeDynamicTypes);

        this.lookupSwitchInstructionNumber = lookupSwitchInstructionNumber;
        this.jumpInstructionNumber = jumpInstructionNumber;
        this.hasJudgeNull = hasJudgeNull;
        this.hasReferenceNull = hasReferenceNull;

        this.methodComment = MethodComment.empty(methodDeclaration.identifier());
    }

    public JigMethod build() {
        return new JigMethod(
                methodDeclaration,
                methodComment,
                hasJudgeNull,
                decisionNumber(),
                annotatedMethods(),
                visibility,
                methodDepend(),
                methodDerivation);
    }

    public MethodDepend methodDepend() {
        return new MethodDepend(useTypes, fieldInstructions, methodInstructions, hasReferenceNull);
    }

    public MethodAnnotations annotatedMethods() {
        List<MethodAnnotation> methodAnnotations = annotations.stream()
                .map(annotation -> new MethodAnnotation(annotation, methodDeclaration))
                .collect(Collectors.toList());
        return new MethodAnnotations(methodAnnotations);
    }

    public DecisionNumber decisionNumber() {
        return new DecisionNumber(jumpInstructionNumber + lookupSwitchInstructionNumber);
    }

    public boolean sameSignature(JigMethodBuilder other) {
        return methodDeclaration.methodSignature().isSame(other.methodDeclaration.methodSignature());
    }

    void collectUsingMethodRelations(List<MethodRelation> collector) {
        for (MethodDeclaration usingMethod : methodInstructions) {
            MethodRelation methodRelation = new MethodRelation(methodDeclaration, usingMethod);
            collector.add(methodRelation);
        }
    }

    public MethodIdentifier methodIdentifier() {
        return methodDeclaration.identifier();
    }

    public void registerMethodAlias(MethodComment methodComment) {
        this.methodComment = methodComment;
    }
}
