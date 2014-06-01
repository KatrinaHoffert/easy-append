These scripts are for use in the releases. The path to the JAR must be updated
accordingly.

The release process is essentially running `mvn install` to obtain the JAR
with bundled dependencies, then bundling that, these scripts, the `resource`
folder, and the `README.md` file into an archive.