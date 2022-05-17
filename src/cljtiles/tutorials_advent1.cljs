(ns cljtiles.tutorials-advent1
  (:require [cljtiles.codeexplode :as explode]
            [clojure.string :as str]
            [cljtiles.utils :as utils]
            [cljs.reader :as edn]))

;; insert tiles/vert after reading the string
;; reload the code when you press the color button
;; show code immediately when puzzle is shown via show Code
;; in curated puzzle, replace run button with "scrambe up the puzzle" random puzzle generatior button, only show green run button when solved
;; move all the defs (chaps etc) into data-store and provide data-store as an atom to components
;; make ->tex-equation work with TeX
;; read 1/2 as 1/2 and not 0.5

;; does not work because beige block only takes one arg, -> introduce additonal brownish block
#_(defn Lc [mass metric coordsys]
  (let [e (coordinate-system->vector-basis coordsys)]
    (fn [[_ x v]]
      ((L2 mass metric) ((point coordsys) x) (* e v)))))
;; make sure you make a try catch in such a case, to that not the whole org file crashes!
;; but make an stdout with the expression

;; look why this nested beige blocks put the F into the wrong place (tutno 2) (defn F-C [F] ...)

;; make sure sucmutils env is loaded so that all puzzles run

;; update spec, respectively replace with malli

;; include load file in context menu (with input field and a default box with urls from Sam's fdg)

;; load exports none, e.g. Cartan is needed, read also crashing #17
;;#+begin_src clojure :noweb yes
;;<<Cartan>>
;;#+end_src

 (def url
   "https://raw.githubusercontent.com/mentat-collective/fdg-book/main/clojure/org/chapter001.org")

(defn generate-content-and-call [txt init-fn]
  (let [tutno 19
        tuts
        (->> (str/split txt #"\#\+end_src")
             (map #(last (str/split % #"\#\+begin_src clojure")))
             (map #(utils/twosplit % "\n"))
             (filter (complement #(str/ends-with? (first %) ":exports none")))
             (map second))
        a (nth tuts tutno)
        b (edn/read-string a)
        c (assoc (explode/explode [b])
                 :solpos-yx [[0 0]]
                 :solution [b])
        content {:tutorials [c] :chapnames ["Advent"] :chaps [(count [c])]}]
    (init-fn [content])))

(defn init-advent [init-fn]
  (-> (js/fetch url)
      (.then #(.text %))
      (.then #(generate-content-and-call % init-fn))))
