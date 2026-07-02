package com.example.citassaludservice.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class ArchitectureTest {

    private static final String BASE = "com.example.citassaludservice";
    private final JavaClasses classes = new ClassFileImporter().importPackages(BASE);

    @Test
    void domain_must_not_depend_on_infrastructure() {
        ArchRule rule = noClasses()
                .that().resideInAPackage(BASE + ".domain..")
                .should().dependOnClassesThat()
                .resideInAPackage(BASE + ".infrastructure..");
        rule.check(classes);
    }

    @Test
    void domain_must_not_depend_on_interface_layer() {
        ArchRule rule = noClasses()
                .that().resideInAPackage(BASE + ".domain..")
                .should().dependOnClassesThat()
                .resideInAPackage(BASE + ".interface_..");
        rule.check(classes);
    }

    @Test
    void application_must_not_depend_on_infrastructure() {
        ArchRule rule = noClasses()
                .that().resideInAPackage(BASE + ".application..")
                .should().dependOnClassesThat()
                .resideInAPackage(BASE + ".infrastructure..");
        rule.check(classes);
    }

    @Test
    void application_must_not_depend_on_interface_layer() {
        ArchRule rule = noClasses()
                .that().resideInAPackage(BASE + ".application..")
                .should().dependOnClassesThat()
                .resideInAPackage(BASE + ".interface_..");
        rule.check(classes);
    }

    @Test
    void infrastructure_must_not_depend_on_interface_layer() {
        ArchRule rule = noClasses()
                .that().resideInAPackage(BASE + ".infrastructure..")
                .should().dependOnClassesThat()
                .resideInAPackage(BASE + ".interface_..");
        rule.check(classes);
    }
}
