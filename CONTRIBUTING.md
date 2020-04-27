Contributing to the QMQP Library
================================

We welcome any kind of contribution.  If you think you've found a bug
or are missing a feature, please
[open an issue](https://github.com/innoq/QMQP-Java/issues); we not
only gladly accept
[pull requests](https://github.com/innoq/QMQP-Java/pulls), we prefer
them for smaller changes.

Before you start working on a big feature, please open an issue and
tell tell us about, though.  This way you can make sure you're not
wasting your time on something that isn't considered to be in the
project's scope.

When creating pull requests, the usual rules of common sense like
"don't mix reformatting code and code changes" apply.  A list we've
stolen from somewhere else describes the process as

Preparing a Pull Request
------------------------

+ Create a topic branch from where you want to base your work (this is
  usually the master branch).
+ Make commits of logical units.
+ Respect the original code style:
  + Only use spaces for indentation.
  + Create minimal diffs - disable on save actions like reformat
    source code or organize imports. If you feel the source code
    should be reformatted create a separate issue/PR for this change.
  + Check for unnecessary whitespace with `git diff --check` before committing.
+ Make sure your commit messages are in the proper format. Your commit
  message should contain the key of the issue if you created one.
+ Make sure you have added the necessary tests for your changes.
+ Run all the tests with `mvn clean test` or to assure nothing else
  was accidentally broken.
