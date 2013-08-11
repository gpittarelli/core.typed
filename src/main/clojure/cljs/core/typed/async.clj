(ns cljs.core.typed.async)

;;;;;;;;;;;;;;;;;;;;;
;;; Typed wrappers
;
;(t/tc-ignore
;(defn ^:private v [vsym]
;  {:pre [(symbol? vsym)
;         (namespace vsym)]}
;  (let [ns (find-ns (symbol (namespace vsym)))
;        _ (assert ns (str "Cannot find namespace: " (namespace vsym)))
;        var (ns-resolve ns (symbol (name vsym)))]
;    (assert (var? var) (str "Cannot find var: " vsym))
;    @var))
;  )
;
;(defmacro go>
;  "Asynchronously executes the body, returning immediately to the
;  calling thread. Additionally, any visible calls to <!, >! and alt!/alts!
;  channel operations within the body will block (if necessary) by
;  'parking' the calling thread rather than tying up an OS thread (or
;  the only JS thread when in ClojureScript). Upon completion of the
;  operation, the body will be resumed.
;
;  Returns a channel which will receive the result of the body when
;  completed"
;  [& body]
;  `(let [c# (chan> ~'Any 1)
;         captured-bindings# (clojure.lang.Var/getThreadBindingFrame)]
;     (tc-ignore
;       (clojure.core.async.impl.dispatch/run
;         (fn []
;           (let [f# ~((v 'clojure.core.async.impl.ioc-macros/state-machine) 
;                      body 1 &env (v 'clojure.core.async.impl.ioc-macros/async-custom-terminators))
;                 state# (-> (f#)
;                            (clojure.core.async.impl.ioc-macros/aset-all! 
;                              clojure.core.async.impl.ioc-macros/USER-START-IDX c#
;                              clojure.core.async.impl.ioc-macros/BINDINGS-IDX captured-bindings#))]
;             (clojure.core.async.impl.ioc-macros/run-state-machine state#)))))
;     c#))
;
;
;(defmacro chan> 
;  "A statically typed core.async channel. 
;
;  (chan> t ...) creates a buffer that can read and write type t.
;  Subsequent arguments are passed directly to clojure.core.async/chan.
;  
;  Note: 
;  (chan> t ...) is the same as ((inst chan t) ...)"
;  [t & args]
;  `((inst clojure.core.async/chan ~t) ~@args))
;
;(defmacro buffer>
;  "A statically typed core.async buffer. 
;
;  (buffer> t ...) creates a buffer that can read and write type t.
;  Subsequent arguments are passed directly to clojure.core.async/buffer.
;  
;  Note: (buffer> t ...) is the same as ((inst buffer t) ...)"
;  [t & args]
;  `((inst clojure.core.async/buffer ~t) ~@args))
;
;(defmacro sliding-buffer>
;  "A statically typed core.async sliding buffer. 
;
;  (sliding-buffer> t ...) creates a sliding buffer that can read and write type t.
;  Subsequent arguments are passed directly to clojure.core.async/sliding-buffer.
;  
;  Note: (sliding-buffer> t ...) is the same as ((inst sliding-buffer t) ...)"
;  [t & args]
;  `((inst clojure.core.async/sliding-buffer ~t) ~@args))
;
;(defmacro dropping-buffer>
;  "A statically typed core.async dropping buffer. 
;  
;  (dropping-buffer> t ...) creates a dropping buffer that can read and write type t.
;  Subsequent arguments are passed directly to clojure.core.async/dropping-buffer.
;  
;  Note: (dropping-buffer> t ...) is the same as ((inst dropping-buffer t) ...)"
;  [t & args]
;  `((inst clojure.core.async/dropping-buffer ~t) ~@args))
