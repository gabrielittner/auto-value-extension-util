Change Log
==========

Version 0.3.0 *(2016-09-09)*
----------------------------

#### Supports: AutoValue 1.3

- it's now recommended to shade this library together with Google's Auto Common (see README)
- AutoValue 1.3 support
- remove deprecated methods in `ElementUtil`

Version 0.2.1 *(2016-06-02)*
----------------------------

#### Supports: AutoValue 1.2

- removed dependency on auto-service

Version 0.2.0 *(2016-05-18)*
----------------------------

#### Supports: AutoValue 1.2

- added `ElementUtil.getMatchingAbstractMethod()` and `ElementUtil.getMatchingStaticMethod()`
    - support matching multiple method parameters
    - new return type `Optional<ExecutableElement>` instead of a nullable `ExecutableElement`
    - `getMatchingAbstractMethod()` will take a `Set<ExecutableElement>` instead of searching methods itself. AutoValue 1.3 will provide a set of abstract methods and for AutoValue 1.2 you can either search yourself or use the old deprecated methods.
- deprecated `getStaticMethod()`, `hasStaticMethod()`, `getAbstractMethod()` and `hasAbstractMethod()` in `ElementUtil` in favor of the mentioned new methods.
- added `Property.buildProperties(Context)`
- added `AutoValueUtil.error()` which will print an error using `Messager` for a given `Property`

Version 0.1.1 *(2016-05-05)*
----------------------------

#### Supports: AutoValue 1.2

- fix ClassName for nested AutoValue classes

Version 0.1.0 *(2016-05-04)*
----------------------------

#### Supports: AutoValue 1.2

Initial release.
