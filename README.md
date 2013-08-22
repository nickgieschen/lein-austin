# lein-austin

Lein-austin is a plugin which adds two subtasks to cljsbuild which facilitate starting up [Austin's](https://github.com/cemerick/austin) two ClojureScript REPL environments.

## Usage

Put `[lein-austin "0.1.0-SNAPSHOT"]` into the `:plugins` vector of your project.clj.

Lein-austin extends cljsbuild with two subtasks, `austin-project` and `austin-browser`, which correspond to [Austin's two ClojureScript REPL environments](https://github.com/cemerick/austin#usage).

### austin-project

`austin-project` creates, in Austin parlance, a project REPL environment (`cemerick.austin/exec-env`.) To launch the simplest form of an Austin project REPL use:

    $ lein trampoline cljsbuild austin-project

`austin-project` supports `exec-env`'s two paraments `:phantom-cmd` and `:exec-cmds`. These can either be specified in the `cljsbuild` section of project.clj or on the command line. An example of specifying them both on the command line is:

    $ lein trampoline cljsbuild austin-project :phantom-cmd slimerjs :exec-cmds '["open" "-ga" "/Applications/Google Chrome.app"]'

In project.clj file each of these keys is prepended with "austin". Thus, the equivalent of the above is:

    $:cljsbuild {:austin-phantom-cmd "slimerjs"
                 :austin-exec-cmds ["open" "-ga" "/Applications/Google Chrome.app"]}
                 
Any options specified on the command line will override those specified in project.clj.                 
                 
### austin-browser

`austin-browser` creates, in Austin parlance, a browser REPL environment (`cemerick.austin/repl-env`.) To launch it use:

    $ lein trampoline cljsbuild austin-browser
    
`austin-browser` requires a function to start the app. This function is specified in the `cljsbuild` section of project.clj under `:austin-start-up`. For example:

    $:cljsbuild {:austin-start-up (cemerick.austin.bcrepl-sample/run)}


## Example

A [copy](https://github.com/nickgieschen/lein-austin/tree/master/browser-connected-repl-sample) of Austin's example project is included to demonstrate the usage of lein-austin.

## License

Copyright © 2013 Nick Gieschen

Distributed under the Eclipse Public License, the same as Clojure.
