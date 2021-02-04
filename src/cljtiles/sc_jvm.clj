(ns cljtiles.sc-jvm
  (:require [sc.impl]
            [sc.impl.db :as db]))

(println "klm in sc-jvmiii")

(defmethod sc.impl/resolve-code-site clojure.lang.Keyword [cs-id]
  (let [csid (->>
               (:code-sites @db/db)
               (filter #(= (:sc.cs/label (val %)) cs-id))
               (map first)
               (apply min))]
    (sc.impl/cs-info csid)))

(comment
  (->>
    (:code-sites @db/db)
    (filter #(= (:sc.cs/label (val %)) :brk2))
    (map first)
    (apply min))

  )
