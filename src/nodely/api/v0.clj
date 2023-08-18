(ns nodely.api.v0
  (:refer-clojure :exclude [cond eval])
  (:require
   [nodely.data]
   [nodely.engine.applicative :as applicative]
   [nodely.engine.core-async.core]
   [nodely.engine.core-async.iterative-scheduling :as iterative-scheduling]
   [nodely.engine.core-async.lazy-scheduling :as lazy-scheduling]
   [nodely.engine.lazy]
   [nodely.engine.manifold]
   [nodely.syntax]
   [nodely.vendor.potemkin :refer [import-fn import-vars]]))

(import-vars nodely.syntax/>cond
             nodely.syntax/>if
             nodely.syntax/>leaf
             nodely.syntax/>and
             nodely.syntax/>or
             nodely.syntax/>value
             nodely.syntax/>sequence
             nodely.engine.core-async.core/>channel-leaf
             nodely.data/value
             nodely.data/leaf
             nodely.data/sequence
             nodely.data/branch)

(import-fn nodely.engine.lazy/eval-node-with-values eval-node-with-values)
(import-fn nodely.data/merge-values merge-values)
(import-fn nodely.data/get-value get-value)

(defn eval
  ([env k]
   (eval env k {}))
  ([env k {engine ::engine
           :or    {engine :core-async.lazy-scheduling}
           :as    opts}]
   (case engine
     :core-async.lazy-scheduling      (lazy-scheduling/eval env k opts)
     :core-async.iterative-scheduling (iterative-scheduling/eval env k opts)
     :async.manifold                  (nodely.engine.manifold/eval env k)
     :async.applicative               (nodely.engine.applicative/eval env k opts)
     :sync.lazy                       (nodely.engine.lazy/eval env k))))

(defn eval-key
  ([env k]
   (eval-key env k {}))
  ([env k {engine ::engine
           :or    {engine :core-async.lazy-scheduling}
           :as    opts}]
   (case engine
     :core-async.lazy-scheduling      (lazy-scheduling/eval-key env k opts)
     :core-async.iterative-scheduling (iterative-scheduling/eval-key env k opts)
     :async.manifold                  (nodely.engine.manifold/eval-key env k)
     :async.applicative               (nodely.engine.applicative/eval-key env k)
     :sync.lazy                       (nodely.engine.lazy/eval-key env k))))

(defn eval-key-channel
  ([env k]
   (eval-key-channel env k {}))
  ([env k {engine ::engine
           :or    {engine :core-async.lazy-scheduling}
           :as    opts}]
   (case engine
     :core-async.lazy-scheduling (lazy-scheduling/eval-key-channel env k opts))))

(defn eval-node
  ([env node]
   (eval-node env node {}))
  ([env node opts]
   (eval-key (assoc env ::target node) ::target opts)))

(defn eval-node-channel
  ([env node]
   (eval-node-channel env node {}))
  ([env node opts]
   (eval-key-channel (assoc env ::target node) ::target opts)))
