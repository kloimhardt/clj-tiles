(ns cljtiles.utils
  (:require [clojure.string :as str]
            [clojure.walk :as w]
            [zprint.core :as zp]
            [goog.string :as gstring]))

(defn find-biggest-in-str [sol puzz]
  ;; sucht grössten gemeinsamen String via LCSubStr
  (some->> (js/LCSubStr sol puzz)
       (re-seq #"(\((.+)\)|\[(.+)\]|\{(.+)\})")
       (map first)
       (group-by count)
       (into (sorted-map))
       last
       val
       first))

(defn stringsearch [ss s]
  (if (seq s)
    (->> (map #(subs ss % (+ % (count s))) (range))
         (take-while seq)
         (take-while #(not= s %))
         count)
    (count ss)))

(defn twosplit-sfn [ss s searchfun]
  (cond
    (nil? ss) [""]
    (nil? s) [ss]
    (not (seq s)) [ss]
    :else
    (let [posi (searchfun ss s)]
      (if (= posi (count ss))
        [ss]
        [(subs ss 0 posi) (subs ss (+ posi (count s)))]))))

(defn twosplit [ss s] (twosplit-sfn ss s stringsearch))

(defn fullsplit-no-tail-recursion [ss s]
  (let [[eins zwo] (twosplit ss s)]
    (if zwo
      (cons eins (fullsplit-no-tail-recursion zwo s))
      (list eins))))

(defn fullsplit-tail-recur [ss s]
  (loop [result []
         [eins zwo] (twosplit ss s)]
    (let [new-result (conj result eins)]
      (if zwo (recur new-result (twosplit zwo s)) new-result))))

(defn fullsplit [ss s]
  (->> [[] (twosplit ss s)]
       (iterate (fn [[result [eins zwo]]]
                  [(conj result eins) (twosplit zwo s)]))
       (drop-while (fn [[_ [_ zwo]]] zwo))
       first
       (apply concat)))

(defn twosplit-word [ss s]
  ;; splittet nur wenn der String s als Wort gefunden wird
  (twosplit-sfn ss s
                (fn [ss s]
                  (if (re-find (re-pattern (str "\\b" s "\\b")) ss)
                    (stringsearch ss s)
                    (count ss)))))

(comment
  (defn uuv [a b]
    [(str/split a b) (twosplit a b)])

  (uuv "a" nil)
  (uuv nil nil)
  (uuv nil "a")
  (uuv "ab" "b") ;; => [["a"] ["a" ""]] case it differs
  (uuv "w- " " ")
  (uuv "a" "b")
  (uuv "ba" "b")
  (uuv "" "a")
  (uuv "a" "")
  (uuv "" "") ;; here it differs as well
  (uuv "(a)" "(a)")
  :end)

;; need to be all the same atringlength
(def white "w-")
(def green "g-")
(def black "b-")
(def yellow "y-")
(def clear "c-")
(assert (= (count clear) (count green) (count white) (count black) (count yellow)))

(defn convert-to-sorted [puzzd-list]
  (->> puzzd-list
       (map #(->> % (w/prewalk (fn [x]
                                 (cond
                                   (map? x) (into (sorted-map) x)
                                   (set? x) (into (sorted-set) x)
                                   :else x)))))))

(defn convert-to-color [puzzd-list sold]
  (let [solds (tree-seq coll? seq sold)]
    (->> puzzd-list
         (map #(->> %
                    (w/prewalk (fn [x]
                                 (let [s (pr-str x)
                                       found? (some #{x} solds)]
                                   (cond
                                     (map-entry? x) x
                                     (coll? x)
                                     (if found?
                                       (str green s)
                                       x)
                                     :else
                                     (if found?
                                       (str yellow s)
                                       (str black s)))))))))))

(defn element-convert-parens [elem]
  (let [open-close-char (fn  [coll]
                          (cond
                            (map? coll) ["{" "}" (apply concat (seq coll))]
                            (vector? coll) ["[" "]" (seq coll)]
                            (set? coll) ["#{" "}" (seq coll)]
                            :else ["(" ")" (seq coll)]))
        [open close leaves] (open-close-char elem)]
    (concat [(str white open)]
            (interpose (str white " ") leaves)
            [(str white close)])))

(defn convert-parens-to-strings [edn-list]
  (->> edn-list
       (map #(->> % (w/prewalk (fn [x]
                                 (if (coll? x)
                                   (element-convert-parens x) x)))))
       (interpose (str white " "))
       flatten))

(defn expand-greens [strings]
  (->> strings
       (mapcat (fn [s] (if (str/starts-with? s green)
                         (->> (str/split (subs s (count green)) #" ")
                              (map #(str green %))
                              (interpose (str green " ")))
                         [s])))))

(comment
  (expand-greens ["g-(1 2 3)" "y-4" "g-(5)"])
;; => ("g-(1" "g- " "g-2" "g- " "g-3)" "y-4" "g-(5)")
  :end)

(defn replace-blanks-with-newline [code colorwords]
  (->> colorwords
       (remove #{white (str white " ")})
       (partition-all 2 1)
       (reduce (fn [[shrinking-code new-colorwords] tuple]
                 (let [[firststr secondstr] (map #(subs % (count green)) tuple)
                       firstpos (+ (stringsearch shrinking-code firststr)
                                   (count firststr))
                       secondpos (+ (stringsearch (subs shrinking-code firstpos) secondstr)
                                    firstpos)
                       blanks (str clear (subs shrinking-code firstpos secondpos))]
                   (if (= firstpos (+ (count shrinking-code) (count firststr))) ;;not found ->brute
                     (do (println "error: coloring stopped as tuple not found " tuple)
                         (reduced [shrinking-code (conj new-colorwords blanks)])) ;;exit, ->
                     ;;results in rest not being colored as here the code is in 'blanks' variable
                     [(subs shrinking-code secondpos) (conj new-colorwords (first tuple) blanks)])))
               [code []])
       peek))

(defn generate-hiccup [strings]
  (letfn [(fgen [s bgc c] [:span {:style {:background-color bgc
                                          :color c}}
                           s])
          (fgreen [s] (fgen s "green" "white"))
          (fwhite [s] (fgen s "white" "black"))
          (fyellow [s] (fgen s "LightYellow" "black"))
          (fblack [s] (fgen s "black" "white"))
          (fclear [s] [:span s])
          (col-dispatch [s] ((get {green  fgreen
                                   white  fwhite
                                   yellow  fyellow
                                   black  fblack
                                   clear fclear}
                                  (subs s 0 (count green)))
                             (subs s (count green))))]
    (map col-dispatch strings)))

(defn convert-to-symbol [sol]
  (->> sol
       (w/postwalk (fn [x]
                     (if (some identity ((juxt keyword? number?) x))
                       (symbol (str x))
                       x)))))

(defn replace-first-green-blank [xs]
  (let [el-type #(subs % 0 (count green))
        greenblank? #(= % (str green " "))
        someclear? #(= (el-type %) clear)]
    (->> xs
         (remove #{clear})
         (partition 2 1 xs)
         (map (fn [[g c]] (if (and (greenblank? g) (someclear? c))
                              (str clear " ")
                              g))))))

(def output-width 41)

(defn code-break-primitive [edn-code]
  (map #(zp/zprint-str % output-width) edn-code))

(defn code->break-str [edn-code]
  (apply str (interpose "\n" (code-break-primitive edn-code))))

(defn render-colored [puzz sol]
  ;;puzz is always a vector of code: => ["Hello, World!"]
  ;;(def code code)
  ;;(def puzz puzz)
  ;;(def sol sol)
  (let [massaged-solution (convert-to-sorted (convert-to-symbol sol))
        massaged-puzzle (convert-to-sorted puzz)]
    (->> (convert-to-color massaged-puzzle massaged-solution)
         (convert-parens-to-strings)
         (expand-greens)
         (replace-blanks-with-newline (code->break-str massaged-puzzle))
         (replace-first-green-blank)
         (generate-hiccup)
         (into [:p {:style {:display "block" :font-family "monospace"
                            :white-space "pre" :margin ["1em" 0]}}]))))

(comment
  (def massaged-solution (convert-to-sorted (convert-to-symbol sol)))
  (def massaged-puzzle (convert-to-sorted puzz)))

