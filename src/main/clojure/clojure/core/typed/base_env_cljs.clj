(ns clojure.core.typed.base-env-cljs
  (:require [clojure.core.typed.base-env-helper-cljs :as h]
            [clojure.core.typed.current-impl :as impl :refer [v]]
            [clojure.core.typed.bootstrap-cljs :as boot]
            [clojure.set :as set]))

(def init-protocol-env 
  (h/protocol-mappings
    
cljs.core/ISeqable [[[x :variance :covariant]]]
cljs.core/ISeq [[[x :variance :covariant]]]
cljs.core/IMap [[[k :variance :covariant]
                 [v :variance :covariant]]]
cljs.core/ISet [[[x :variance :covariant]]]
cljs.core/IVector [[[x :variance :covariant]]]
cljs.core/IList [[[x :variance :covariant]]]
    
    ))

; add protocols to environment to parse rest of file
(impl/with-cljs-impl
  ((v 'clojure.core.typed.protocol-env/reset-protocol-env!) 
   init-protocol-env))

(def init-var-env
  (merge
    (h/var-mappings

;internal vars
cljs.core.typed/ann* [Any Any -> Any]
cljs.core.typed/ann-protocol* [Any Any Any -> Any]
cljs.core.typed/ann-datatype* [Any Any Any Any -> Any]
cljs.core.typed/def-alias* [Any Any -> Any]

cljs.core/+ [number * -> number]
      )))

(def init-var-nochecks
  (set (keys init-var-env)))

(def init-alias-env 
  (h/alias-mappings

  ^{:doc "A type that returns true for clojure.core/integer?"}
cljs.core.typed/AnyInteger int
    
    ^{:doc "A type that can be used to create a sequence of member type x."}
cljs.core.typed/Seqable (TFn [[x :variance :covariant]]
                             (cljs.core/ISeqable x))
    ))

; Ensure init-alias-env agrees with the -base-aliases

(assert (= (set (keys init-alias-env))
           (set (map #(symbol "cljs.core.typed" (str %))
                     boot/-base-aliases)))
        (str "core.typed Bug! Base aliases do not agree with base environment."
             " Missing from core.typed ns: "
             (set/difference (set (keys init-alias-env))
                             (set (map #(symbol "cljs.core.typed" (str %))
                                       boot/-base-aliases)))
             " Missing from base-env ns "
             (set/difference (set (map #(symbol "cljs.core.typed" (str %))
                                       boot/-base-aliases))
                             (set (keys init-alias-env)))))



(def init-declared-kinds {})

(defn reset-cljs-envs! []
  (impl/with-cljs-impl
    ((v 'clojure.core.typed.name-env/reset-name-env!) init-alias-env)
    ((v 'clojure.core.typed.var-env/reset-var-type-env!)
     init-var-env init-var-nochecks)
    ((v 'clojure.core.typed.protocol-env/reset-protocol-env!) 
     init-protocol-env)
    ((v 'clojure.core.typed.declared-kind-env/reset-declared-kinds!) 
     init-declared-kinds))
  nil)
