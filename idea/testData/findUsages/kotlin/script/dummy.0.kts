// PSI_ELEMENT: org.jetbrains.kotlin.psi.KtProperty
// OPTIONS: usages
val <caret>foo = "foo"
val bar = foo

// ERROR: No script runtime was found in the classpath: class 'kotlin.script.templates.standard.ScriptTemplateWithArgs' not found. Please add kotlin-script-runtime.jar to the module dependencies.