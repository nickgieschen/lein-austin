(ns lein-austin.plugin
  (:require [leiningen.help :as lhelp]
            [leiningen.core.eval :as leval]
            [leiningen.core.main :as lmain]
            [leiningen.cljsbuild.config :as cljsbuild-config]
            [leiningen.cljsbuild :as cljsbuild]
            robert.hooke))

;; Unabashedly ripped off from lein-ring.
(defn load-namespaces
  "Create require forms for each of the supplied symbols. This exists because
  Clojure cannot load and use a new namespace in the same eval form."
  [& syms]
  `(require
     ~@(for [s syms :when s]
         `'~(if-let [ns (namespace s)]
              (symbol ns)
              s))))

(defn- repl-austin-project
       "Run an austin project REPL."
       [project {crossover-path :crossover-path builds :builds start-up :start-up}]
       (cljsbuild/require-trampoline
         (#'cljsbuild/run-local-project project crossover-path builds
          (load-namespaces 'cemerick.austin.repls)
          `(cemerick.austin.repls/exec)
          )))

(defn- build-browser-commands [start-up]
       `(do
          ~@(list start-up)
          (def ~'repl-env (reset! cemerick.austin.repls/browser-repl-env (cemerick.austin/repl-env)))
          (cemerick.austin.repls/cljs-repl ~'repl-env)))

(defn- repl-austin-browser
       "Run an austin browser REPL."
       [project {crossover-path :crossover-path builds :builds start-up :start-up}]
       (cljsbuild/require-trampoline
         (#'cljsbuild/run-local-project project crossover-path builds
          (load-namespaces (first start-up) 'cemerick.austin.repls 'cemerick.austin) ;; todo go through start-up recursively grabbing namespaces in case its in a do block
          (build-browser-commands start-up))))

(defn intercept-repl-austin [f & args]
  (let [project (first args)
        subtask (second args)
        options (#'cljsbuild-config/extract-options project)]
    (case subtask
      "repl-austin-project" (repl-austin-project project options)
      "repl-austin-browser" (repl-austin-browser project options)
      (apply f args)))) ; Continue with standard cljsbuild

;; todo get this working
;; This will get called once when lein loads our plugin.
(alter-meta! #'cljsbuild/cljsbuild
             (fn [meta]
               (-> meta
                   (update-in [:subtasks] conj #'repl-austin-project)
                   (update-in [:help-arglists]
                              (fn [arglists] (list (conj (first arglists) 'repl-austin-project)))))))

(defn hooks []
  (robert.hooke/add-hook #'cljsbuild/cljsbuild intercept-repl-austin))
