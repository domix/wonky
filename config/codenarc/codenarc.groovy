// File: config/codenarc/rules.groovy

ruleset {
  ruleset('rulesets/basic.xml')
  ruleset('rulesets/braces.xml')
  ruleset('rulesets/concurrency.xml')
  ruleset('rulesets/convention.xml')
  ruleset('rulesets/design.xml') {
    exclude 'AbstractClassWithoutAbstractMethod'
    exclude 'ConstantsOnlyInterface'
    exclude 'PrivateFieldCouldBeFinal'
  }
  ruleset('rulesets/dry.xml') {
    exclude 'DuplicateNumberLiteral'
  }
  ruleset('rulesets/enhanced.xml')
  ruleset('rulesets/exceptions.xml') {
    exclude 'CatchException'
  }
  ruleset('rulesets/formatting.xml') {
    exclude 'ClassJavadoc'
    exclude 'SpaceAroundMapEntryColon'
  }
  ruleset('rulesets/generic.xml')
  ruleset('rulesets/groovyism.xml')
  ruleset('rulesets/imports.xml') {
    exclude 'NoWildcardImports'
  }
  ruleset('rulesets/jdbc.xml')
  ruleset('rulesets/logging.xml') {
    'Println' priority: 1
    'PrintStackTrace' priority: 1
  }
  ruleset('rulesets/naming.xml')
  ruleset('rulesets/security.xml')
  ruleset('rulesets/serialization.xml') {
    exclude 'SerializableClassMustDefineSerialVersionUID'
  }
  ruleset('rulesets/size.xml')
  ruleset('rulesets/unnecessary.xml')
  ruleset('rulesets/unused.xml')
}