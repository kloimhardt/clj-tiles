#_"SPDX-License-Identifier: GPL-3.0"

(ns cljtiles.sc-jvm
  (:require [sc.impl]
            [sc.impl.db :as db]))

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
