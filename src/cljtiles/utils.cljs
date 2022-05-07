(ns cljtiles.utils
  (:require [clojure.string :as str]
            [clojure.walk :as w]
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

(defn fullsplit [ss s]
  (let [[eins zwo] (twosplit ss s)]
    (if zwo (cons eins (fullsplit zwo s)) (list eins))))

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

(defn convert-to-color [puzzd-list sold]
  (let [solds (tree-seq coll? seq sold)]
    (map (fn [puzzd]
           (w/prewalk (fn [x]
                        (let [s (pr-str x)
                              found? (some #{x} solds)]
                          (if (coll? x)
                            (if found?
                              (str green s)
                              x)
                            (if found?
                              (str yellow s)
                              (str black s)))))
                      puzzd))
         puzzd-list)))

(defn element-convert-parens [elem]
  (let [open-close-char (fn  [coll]
                          (cond
                            (map? coll) ["{" "}"]
                            (list? coll) ["(" ")"]
                            (vector? coll) ["[" "]"]
                            (set? coll) ["#{" "}"]))
        [open close] (open-close-char elem)]
    (concat [(str white open)]
            (interpose (str white " ") (seq elem))
            [(str white close)])))

(defn convert-parens-to-strings [edn-list]
  (->> edn-list
       (map (fn [edn]
                  (w/prewalk (fn [x]
                               (if (coll? x)
                                 (element-convert-parens x) x))
                             edn)))
       (interpose (str white " "))
       flatten))

(defn expand-greens [strings]
  (map (fn [s] (if (str/starts-with? s green)
                 (->> (str/split (subs s (count green)) #" ")
                      (map #(str green %))
                      (interpose (str green " ")))
                 s))
       strings))

(defn detect-real-planks [code [tuple & r]]
  (let [[firststr secondstr] (map #(subs % (count green)) tuple)
        firstpos (+ (stringsearch code firststr) (count firststr))
        secondpos (+ (stringsearch (subs code firstpos) secondstr) firstpos)
        blanks (str clear (subs code firstpos secondpos))]
    (if r
      (if (= firstpos (+ (count code) (count firststr)))
        (cons clear (detect-real-planks code r))
        (cons blanks (detect-real-planks (subs code secondpos) r)))
      (list blanks))))

(defn replace-blanks-with-newline [code colorwords]
  (let [words-no-blanks (remove #(= % (str white " ")) colorwords)]
    (interleave words-no-blanks
                (detect-real-planks code (partition-all 2 1 words-no-blanks)))))

(defn generate-hiccup [strings]
  (letfn [(fgen [s bgc c] [:span {:style {:background-color bgc
                                          :color c}}
                           s])
          (fgreen [s] (fgen s "green" "white"))
          (fwhite [s] (fgen s "white" "black"))
          (fyellow [s] (fgen s "LightYellow" "black"))
          (fblack [s] (fgen s "black" "white"))
          (fclear [s]
            (into [:span]
                  (map #(case %
                          " " (gstring/unescapeEntities "&nbsp;")
                          "\n" [:br]
                          [:span %])
                       (seq s))))
          (col-dispatch [s] ((get {green  fgreen
                                   white  fwhite
                                   yellow  fyellow
                                   black  fblack
                                   clear fclear}
                                  (subs s 0 (count green)))
                             (subs s (count green))))]
    (map col-dispatch strings)))

(defn render-colored [code puzz sol]
  ;;puzz is always a vector of code: => ["Hello, World!"]
  (->> (convert-to-color puzz sol)
       (convert-parens-to-strings)
       (expand-greens)
       (flatten)
       (replace-blanks-with-newline code)
       (generate-hiccup)
       (into [:p {:style {:display "block" :font-family "monospace"
                          :white-space "pre" :margin ["1em" 0]}}])))
