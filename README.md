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
they must appear). All except the `--prepend`/`--append` are optional.

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

* `--same-line` - If this flag is present, the text addition will be done on the
                  same line instead of putting it on its own line (the default).

* `--file` - In the form of `--file=<path>`, where path is the path to some
             file that contains the text for this text addition. Only one of
             `--file` or `<text to add>` (at the end of the block) may appear.

##Program options

These commands can be in any order.

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

* `--charset` - Use as `charset=<encoding>`, where encoding is a string for the
                particular encoding as described on
                [this page](http://goo.gl/X5ClxW). If not specified, the default
                is UTF-8. Needs to be specified before any text additions that
                read from files.

##Installation

This program requires Maven. Installation is as simple as running `mvn install`
in the directory with the `pom.xml` file, and then using a command such as the
one below to run the program.

```bash
java -jar target/easy-append-0.0.1-SNAPSHOT-jar-with-dependencies.jar
```

Obviously replacing the path to the file with the output of `mvn install`.

##Examples

###Simple addition

Prepend and append plain some text to files:

```bash
--prepend "prepended text" --append "appended text" file1.txt file2.txt
```

###Using conditionals

Prepend text if there is a line containing just a number:

```bash
--prepend --contains="^[0-9]+$" "prepended text" file.txt
```

The inverse of above (prepending if there is no such line):

```bash
--prepend --contains="^[0-9]+$" --invert "prepended text" file.txt
```

###Stacking additions

Multiple text additions stack. So if we have a file, `file.txt`, that contains:

```
A
B
C
```

And run the following command on it:

```bash
--prepend "1" --prepend "2" --prepend "3" file.txt
```

Then the file content would become:

```
1
2
3
A
B
C
```

###Storing additions in files

We could obtain the text to be added from a file instead of specifying it as an
argument. Helpful for large amounts of text:

```bash
--prepend --file=prepend_text.txt file.txt
```

###Specifying an alternative location to place files

Also useful is the ability to specify a location to save the output files. If
this alternative location is not specified, the original files will be
overwritten. Directories are created in an intelligent manner to ensure that
there will not be any conflicts amongst the files. This is useful for previewing
your changes without overwriting the original files.

In this example, we will also be using absolute paths to demonstrate the folder
structure created.

Assume that we have the following files that we want to make additions to:

```
C:\a\b\file.txt
C:\a\file.txt
F:\a\file.txt
```

We could modify these and tell Easy Append to place the output in `C:\output`
with:

```bash
--location="C:\output" --prepend "X" C:\a\b\file.txt C:\a\file.txt F:\a\file.txt
```

After running this, the contents of `C:\output` will be:

```
C:\output\c\a\b\file.txt
C:\output\c\a\file.txt
C:\output\f\a\file.txt
```

On the other hand, suppose that we only were making additions to the following
files:

```
C:\a\b\file.txt
C:\a\file.txt
```

In which case, the similar command would cause the contents of `C:\output` to
be:

```
C:\output\b\file.txt
C:\output\file.txt
```

As we can see, the program determines the minimum number of directories we need
to ensure there is no ambiguity.

###Dry run

To avoid issues that typos and such could raise, performing a dry run before
making complicated text additions is a good idea. The `--dry-run` flag will
cause the program to simulate what would happen without writing any files.

Suppose we had two files, `test.txt` and `test_2.txt`, with the following
content, respectfully:

```
A
B
C
```

```
D
E
F
```

Then we run this command to perform some text additions (note that the slash
is not part of the command -- it's handled by the shell to escape the line
break in our command):

```bash
--dry-run --prepend --contains="A" "Text" --append --contains="D" "Text" \
    test.txt test_2.txt
```

Would output (to standard output):

```
Working on file test.txt
   Evaluating text addition #1 (prepend)
      File does contain the regex.
      Text will be prepended.
   Evaluating text addition #2 (append)
      File does not contain the regex.
      Skipping because regex should be matched.
Working on file test_2.txt
   Evaluating text addition #1 (prepend)
      File does not contain the regex.
      Skipping because regex should be matched.
   Evaluating text addition #2 (append)
      File does contain the regex.
      Text will be appended.
```