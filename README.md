#Easy Append
**Simple and fast bulk text additions**

##What is Easy Append?
Easy Append is a program for performing bulk prepending and appending to text
files, with support for advanced features such as conditional additions (for
example, only adding a license header if the file does not already have one).

##Usage
Easy Append uses command line arguments to detail a list of text additions we
want to add and a list of files to add. The command line arguments for the text
additions come in "blocks", starting with a mere `--prepend` or `--append`,
followed by additional arguments that apply only to the last `--prepend` or
`--append`.

The general usage is:
    `--prepend [--contains=<regex> [--invert]] <text to add> <files>`

For example, to specify to prepend "foo" to a file that does not contain a line
with "A", we'd use `--prepend --contains="^A$" --invert "foo"`. Note that the
quotes are not part of the syntax, but rather part of the shell (and allow us
to include whitespace in our arguments.

The full list of available arguments in text additions are (in the order that
they must appear):

* `--prepend` - Prepends text to the beginning of files.

* `--append` - Appends text to the end of files. Only one of either `--prepend`
               or `--append` may appear.

* `--contains` - In the form of `--contains=<regex>`, where regex is a regular
                 expression that must be matched in the file to perform the text
                 addition. If no match is found, the addition is not performed
                 on that file (useful for adding text iff the file does not
                 already contain the text).

* `--invert` - If this flag is present, the regex is inverted and the file must
               *not* contain the regex to have the text addition applied.

* `--file` - In the form of `--file=<path>`, where path is the path to some
             file that contains the text for this text addition. Only one of
             `--file` or `<text to add>` (at the end of the block) may appear.

##Program options

* `--verbose` - Display additional information about the program's process.

* `--dry-run` - Show what the program would do without any actual side effects.
                If this is on, `--verbose` is implied.

* `--help` - Displays this text.

* `--recursive` - If enabled, including a directory in the file list will result
                  in all files inside that directory (and in sub directories)
                  being added.

* `--location` - Allows written files to be placed in an alternative location
                 instead of overwriting the originals. To prevent conflicts,
                 some folders will be retained in the new location. Note that if
                 files exist in this alternative location, they may be
                 overwritten. Use in the format `--location=<path>`.

##Installation

Coming soon. For now, the project requires Eclipse, Maven, and Eclipse's m2e
plugin to be run.