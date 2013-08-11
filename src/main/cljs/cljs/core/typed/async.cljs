(ns 
  ^{:doc 
    "This namespace contains annotations and helper macros for type
    checking core.async code. Ensure clojure.core.async is require'd
    before performing type checking.
    
    go
      use go>

    chan
      use chan>

    buffer
      use buffer> (similar for other buffer constructors)
    "}
  cljs.core.typed.async
  (:require-macros [cljs.core.typed :refer [ann ann-datatype def-alias ann-protocol inst
                                            tc-ignore]
                    :as t])
  (:require [cljs.core.typed :refer [AnyInteger Seqable]]))

;TODO how do we encode that nil is illegal to provide to Ports/Channels?
;     Is it essential?

;;;;;;;;;;;;;;;;;;;;
;; Protocols

(ann-protocol [[w :variance :contravariant]
               [r :variance :covariant]]
              clojure.core.async.impl.protocols/Channel)

(ann-protocol [[r :variance :covariant]]
              clojure.core.async.impl.protocols/ReadPort)

(ann-protocol [[w :variance :contravariant]] 
              clojure.core.async.impl.protocols/WritePort)

(ann-protocol [[x :variance :invariant]]
               clojure.core.async.impl.protocols/Buffer)

(ann-datatype [[w :variance :covariant]
               [r :variance :contravariant]]
              clojure.core.async.impl.channels.ManyToManyChannel 
              []
              :unchecked-ancestors #{(clojure.core.async.impl.protocols/Channel w r)
                                     (clojure.core.async.impl.protocols/ReadPort r)
                                     (clojure.core.async.impl.protocols/WritePort w)})

;;;;;;;;;;;;;;;;;;;;;
;;; Aliases

(def-alias ReadOnlyChan
  "A core.async channel that statically disallows writes."
  (TFn [[r :variance :covariant]]
    (Extends [(clojure.core.async.impl.protocols/WritePort Nothing)
              (clojure.core.async.impl.protocols/ReadPort r)
              (clojure.core.async.impl.protocols/Channel Nothing r)])))

(def-alias Chan
  "A core.async channel"
  (TFn [[x :variance :invariant]]
    (Extends [(clojure.core.async.impl.protocols/WritePort x)
              (clojure.core.async.impl.protocols/ReadPort x)
              (clojure.core.async.impl.protocols/Channel x x)])))

(def-alias TimeoutChan
  "A timeout channel"
  (Chan Any))

(def-alias Buffer
  "A buffer of type x."
  (TFn [[x :variance :invariant]]
    (clojure.core.async.impl.protocols/Buffer x)))

(def-alias ReadOnlyPort
  "A read-only port that can read type x"
  (TFn [[r :variance :covariant]]
    (Extends [(clojure.core.async.impl.protocols/ReadPort r) 
              (clojure.core.async.impl.protocols/WritePort Nothing)])))

(def-alias WriteOnlyPort
  "A write-only port that can write type x"
  (TFn [[x :variance :invariant]]
    (Extends [(clojure.core.async.impl.protocols/ReadPort x) 
              (clojure.core.async.impl.protocols/WritePort x)])))

(def-alias Port
  "A port that can read and write type x"
  (TFn [[x :variance :invariant]]
    (Extends [(clojure.core.async.impl.protocols/ReadPort x) 
              (clojure.core.async.impl.protocols/WritePort x)])))

;;;;;;;;;;;;;;;;;;;;;
;;; Var annotations

(ann ^:no-check clojure.core.async/buffer (All [x] [AnyInteger -> (Buffer x)]))
(ann ^:no-check clojure.core.async/dropping-buffer (All [x] [AnyInteger -> (Buffer x)]))
(ann ^:no-check clojure.core.async/sliding-buffer (All [x] [AnyInteger -> (Buffer x)]))

(ann ^:no-check clojure.core.async/thread-call (All [x] [[-> x] -> (Chan x)]))

(ann ^:no-check clojure.core.async/timeout [AnyInteger -> TimeoutChan])

(ann ^:no-check clojure.core.async/chan (All [x] 
                                            (Fn [-> (Chan x)]
                                                [(U (Buffer x) AnyInteger) -> (Chan x)])))
;(ann clojure.core.async/>! (All [x] [(Chan x) -> (Chan x)]))

;(ann ^:no-check clojure.core.async.impl.ioc-macros/aget-object [AtomicReferenceArray AnyInteger -> Any])
;(ann ^:no-check clojure.core.async.impl.ioc-macros/aset-object [AtomicReferenceArray Any -> nil])
;(ann ^:no-check clojure.core.async.impl.ioc-macros/run-state-machine [AtomicReferenceArray -> Any])

;FIXME what is 2nd arg?
(ann ^:no-check clojure.core.async.impl.ioc-macros/put! (All [x] [AnyInteger Any (Chan x) x -> Any]))
;(ann ^:no-check clojure.core.async.impl.ioc-macros/return-chan (All [x] [AtomicReferenceArray x -> (Chan x)]))

(ann ^:no-check clojure.core.async/<!! (All [x] [(ReadOnlyPort x) -> (U nil x)]))
(ann ^:no-check clojure.core.async/>!! (All [x] [(WriteOnlyPort x) x -> nil]))
(ann ^:no-check clojure.core.async/alts!! 
     (All [x d]
          (Fn [(Seqable (U (Port x) '[(Port x) x])) (Seqable (Port x)) & :mandatory {:default d} :optional {:priority (U nil true)} -> 
               (U '[d ':default] '[x (Port x)])]
              [(Seqable (U (Port x) '[(Port x) x])) & :optional {:priority (U nil true)} -> '[x (Port x)]])))

(ann ^:no-check clojure.core.async/close! [(ReadOnlyChan Any) -> nil])

;(ann ^:no-check clojure.core.async.impl.dispatch/run [[-> (ReadOnlyChan Any)] -> Executor])
;(ann clojure.core.async.impl.ioc-macros/async-chan-wrapper kV

