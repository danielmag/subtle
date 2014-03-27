run using: java -jar Subtle.jar

The configuration file is in resources/config.xml
Below is a list of configurable properties and their meaning.

subtitles: The location of the subtitle files. Can have a relative or an absolute path. The files may be just text or encripted in .gz.
timeDiff: Maximum difference of time that two consecutive utterances may have to be considered an Interaction-Response pair. If the value is set to 0 than all consecutive utterances will be considered Interaction-Response pairs.
toTag: Boolean value. If set to true, the each utterance in the corpora will be annotated with the chosen classifier.
serializedClassifier: If toTag is set to true, the user needs set the location of the classifier here. Can have a relative or an absolute path.