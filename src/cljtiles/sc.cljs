#_"SPDX-License-Identifier: GPL-3.0"

(ns cljtiles.sc
  (:require [sc.impl]))

(defmethod sc.impl/find-ep cljs.core/Keyword
  [db ep-id]
  (let [csid
        (->> (:code-sites db)
             (filter #(= (:sc.cs/label (val %)) ep-id))
             (map first)
             (apply min))
        epid
        (->> db
             :execution-points
             (filter #(= (get-in (val %) [:sc.ep/code-site :sc.cs/id]) csid))
             (map first)
             (apply max))]
    (-> db :execution-points (get epid)
        (or (throw (ex-info (str "No Execution Point with ID " epid)
                            {:sc.api.error/error-type
                             :sc.api.error-types/no-ep-found-with-id
                             :sc.ep/id epid
                             :ep-id ep-id}))))))

(comment
  (-> dbi :execution-points)
  (sc.api/defsc :brk2)
  (sort > (keys (get-in dbi [:execution-points])))
  (keys (get-in dbi [:code-sites]))

  (let [csid
        (->> (:code-sites dbi)
             (filter #(= (:sc.cs/label (val %)) :brk2))
             (map first)
             (apply min))
        epid
        (->> dbi
             :execution-points
             (filter #(= (get-in (val %) [:sc.ep/code-site :sc.cs/id]) csid))
             (map first)
             (apply max))]))


