# AutoValue: Extension Utilities

A set of utilities to make writing extensions for Google's [AutoValue][auto] and reduce boilerplate code.

- **Property**: A nicer way to work with the properties of the annotated class
- **AutoValueUtil**: Convenience methods for the code you have to generate.
- **ElementUtil**: Convenience methods around `Element`

## Shading

This project depends on Google's [Auto Common Utilities][common] and recommends to shade it:
> Users of auto-common are urged to use shade or jarjar (or something similar) in packaging their processors
> so that conflicting versions of this library do not adversely interact with each other.
[Source][shade]

It is recommended that you shade this library together with Auto Common in your extension.

## Download

Add a Gradle dependency:

```groovy
compile 'com.gabrielittner.auto.value:auto-value-extension-util:0.4.0'
```

or Maven:
```xml
<dependency>
  <groupId>com.gabrielittner.auto.value</groupId>
  <artifactId>auto-value-extension-util</artifactId>
  <version>0.4.0</version>
  <scope>compile</scope>
</dependency>
```

Snapshots of the development version are available in [Sonatype's `snapshots` repository][snap].

## License


```
Copyright 2016 Gabriel Ittner.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```



 [auto]: https://github.com/google/auto
 [common]: https://github.com/google/auto/tree/master/common
 [shade]: https://github.com/google/auto/tree/master/common#processor-resilience
 [snap]: https://oss.sonatype.org/content/repositories/snapshots/
