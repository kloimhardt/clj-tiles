(ns cljtiles.view
  (:require
   [goog.dom :as gdom]
   [goog.string :as gstring]
   [goog.dom.forms :as gforms]
   [sci.core :as sci]
   [sicmutils.env.sci :as es]
   ["blockly" :as blockly]
   [cljtiles.xmlparse-2 :as edn->code]
   [cljtiles.tutorials-clj :as t-0]
   [cljtiles.genblocks :as gb]
   [cljtiles.tutorials-sicm :as t-s]
   [cljtiles.tutorials-sicm2 :as t-s2]
   [cljtiles.tutorials-sicm3 :as t-s3]
   [cljs.reader :as edn]
   [clojure.walk :as w]
   [tubax.core :as sax]
   [reagent.core :as rc]
   [reagent.dom :as rd]
   [zprint.core :as zp]
   [cljtiles.sicm :as sicm]
   [cljtiles.blockly :as workspace!]
   [cljtiles.code-analysis :as ca]

   ;;[cljtiles.tests :as tst]
   ;;[sc.api]
   ;;[cljtiles.sc]
   ))

#_(when workspace!/dev
  (print (tst/test-pure)))

(defn generate-xml [page]
  (if (:xml-code page)
    page
    (assoc page :xml-code (apply gb/rpg (:blockpos page) (:code page)))))

(def content (let [f (fn [ks v]
                       (reduce #(assoc %1 %2 (mapcat %2 v)) {} ks))]
               (-> (f [:tutorials :chapnames :chaps]
                      [t-0/content t-s2/content t-s/content t-s3/content])
                   (update :tutorials #(map generate-xml %)))))

(def tutorials (:tutorials content))
(def chaps (:chaps content))
(def chapnames (:chapnames content))

(defn page->chapter [page-no]
  (- (count chaps) (count (filter #(> % page-no) (reductions + chaps)))))

(defn chapter->page [chap-no]
  (if (pos? chap-no) (nth (reductions + chaps) (dec chap-no)) 0))

(defn load-workspace [xml-text]
  (.. blockly/Xml
      (clearWorkspaceAndLoadFromXml (.. blockly/Xml (textToDom xml-text))
                                    (.getMainWorkspace blockly))))

(defn append-to-workspace [xml-text]
  (.. blockly/Xml
      (appendDomToWorkspace (.. blockly/Xml (textToDom xml-text))
                            (.getMainWorkspace blockly))))

(defonce state (rc/atom nil))

(defn reset-state [tutorial-no]
  (let [tn (:tutorial-no @state)
        ds (:desc @state)
        init {:stdout []
              :inspect []
              :sci-error nil
              :sci-error-full nil
              :result-raw nil
              :code nil
              :edn-code nil
              :edn-code-orig nil
              :modal-style-display "none"
              :run-button true
              :tutorial-no tn
              :desc ds}
        check (or (not @state) (= (into (hash-set) (keys init))
                                  (into (hash-set) (keys @state))))]
    (reset! state (merge init
                         (when tutorial-no
                           {:tutorial-no tutorial-no
                            :desc (:description (nth tutorials tutorial-no))})))
    (when-not check
      (swap! state assoc :stdout ["state is not in best state, pls. report."]))
    ))

(defonce app-state (rc/atom nil))

(defn set-scrollbar [x y]
  (when x
    (.. blockly -mainWorkspace (scroll x y))))

(defn goto-page! [page-no]
  (load-workspace (:xml-code (nth tutorials page-no)))
  (apply set-scrollbar (:scroll (nth tutorials page-no)))
  (gforms/setValue (gdom/getElement "tutorial_no") page-no)
  (reset-state page-no)
  (reset! app-state 0)
  page-no)

(defn goto-lable-page! [lable]
  (let [cnt (count (take-while false? (map #(= lable (:lable %)) tutorials)))]
    (when (< cnt (count tutorials)) (goto-page! cnt))))

(defn tutorial-fu [inc-or-dec]
  (fn []
    (let [el (gdom/getElement "tutorial_no")
          idx-old  (gstring/toNumber (gforms/getValue el))
          idx-new (inc-or-dec idx-old)
          idx (cond
                (< -1 idx-new (count tutorials)) idx-new
                (> 0 idx-new) 0
                (< (dec (count tutorials)) idx-new) (dec (count tutorials))
                (< -1 idx-old (count tutorials)) idx-old
                :else 0)]
      (goto-page! idx))))

(def output-width 41)

(defn code->break-str [edn-code]
  (apply str (interpose "\n" (map #(zp/zprint-str % output-width) edn-code))))

(defn part-str [width s]
  (apply str
         (interpose "\n"
                    (map (partial apply str)
                         (partition-all width s)))))

(defn augment-code-fu [edn-code flat-code fn-code]
  (if (seq (filter #{(second fn-code)} flat-code))
    (into [] (cons fn-code edn-code))
    edn-code))

(defn start-with-div? [last-edn-code]
  (and (vector? last-edn-code)
       (= ":div" (subs (str (first last-edn-code)) 0 4))))

(defn tex-comp [txt]
  [:div {:ref (fn [el] (try (.Queue js/MathJax.Hub
                                    #js ["Typeset" (.-Hub js/MathJax) el])
                            (catch js/Error e (println (.-message e)))))}
   txt])

(defn html-tex-comp [e]
  [tex-comp (sicm/tex e)])

(def reagent-component-bindings
  {'tex html-tex-comp})

(defn parse-:div> [e]
  (let [sf (str (first e))
        sb (symbol (subs sf 5))]
    (cond-> e
      (and (= ":div>" (subs sf 0 5)) (get reagent-component-bindings sb))
      (assoc 0 sb))))

(defn augment-code-div [edn-code inspect-fn]
  (let [l (last edn-code)]
    (if (and (not inspect-fn) (start-with-div? l))
      (conj (into [] (butlast edn-code))
            (list 'defn 'cljtiles-reagent-component
                  "added by clj-tiles parser"
                  []
                  (parse-:div> l))
            'cljtiles-reagent-component)
      edn-code)))

(defn augment-code [edn-code inspect-fn]
  (let [flat-code (flatten (w/postwalk #(if (map? %) (vec %) %) edn-code))]
    (-> edn-code
        (augment-code-fu flat-code
                         '(defn vec-rest "added by clj-tiles parser" [x]
                            (let [r (rest x)] (if (seq? r) (vec r) r))))
        (augment-code-fu flat-code
                         '(defn vec-cons "added by clj-tiles parser" [x coll]
                            (let [c (cons x coll)] (if (seq? c) (vec c) c))))
        (augment-code-fu flat-code
                         '(defn L-free-particle "added by clj-tiles parser" [x]
                            (comp sicmutils-double (L-free-particle-sicm x))))
        (augment-code-div inspect-fn))))

(defn start-timer [fu ms max msg]
  (let [timer (atom nil)
        counter (atom 0)
        stop-timer (fn [msg]
                     (js/clearInterval @timer)
                     msg)
        step (fn []
               (swap! counter inc)
               (if (< @counter max) (fu nil) (stop-timer nil)))]
    (reset! timer (js/setInterval step ms))
    msg))

(defn bindings [new-println tex-inspect]
  (merge
   (es/namespaces 'sicmutils.numerical.minimize)
   (es/namespaces 'sicmutils.mechanics.lagrange)
   (es/namespaces 'sicmutils.env)
   (es/namespaces 'sicmutils.abstract.function) ;;needs to be after sicmutils.env
   sicm/bindings
   reagent-component-bindings
   {'println new-println
    'html-tex html-tex-comp
    workspace!/inspect-fn-sym tex-inspect
    'app-state app-state
    'start-timer start-timer}))

;; this executes every form one by one, no matter whether an error occurs
;; lets inspect expressions after an error
;; would also have the advantage of showing the last valid result
;; if there were not this stack overflow problem
;; but very useful for inspect, so it is used there
(defn cljtiles-eval-one-by-one [edn-code bindings]
  (let [the-errs (atom [])
        the-env (atom nil)
        sci-eval (fn [code-str env errs]
          (try (sci/eval-string (str code-str)  {:bindings bindings :env env})
               (catch js/Error e (swap! errs conj e) :sci-error)))
        cbr (map #(zp/zprint-str % output-width) edn-code)
        _results (doall (map #(sci-eval % the-env the-errs) cbr))
        err-msgs (map #(.-message %) @the-errs)
        ;; cbr-noflines (map (fn [y] (inc (count (filter (fn [x] (= "\n" x)) y)))) cbr)
        ;; cbr-sumlines (reductions + (cons 0 cbr-noflines))
        ;; err-lines (map #(.-data %) @the-errs)
        ;;first-err-num (count (take-while #(not= % :sci-error) results))
        ;;!!!! this produces a stack overflow as comaring to sicm expressions seems to be very expensive !!!
        ;;err-correct-line (update (first err-lines) :line #(+ % (nth cbr-sumlines first-err-num)))
        ;; takes last good result
        ;;good-results (drop-while #(= % :sci-error) (reverse results))
        ;;good-result (first good-results)

        ;; takes result before first error
        ;;good-results (take-while #(not= % :sci-error) results)
        ;;good-result (last good-results)
        ]
    {:result nil #_{:expression good-result
              :number (dec (count good-results))
              :line (inc (nth cbr-sumlines (dec (count good-results)) -1))}
     :err {:message (first err-msgs)
           :line nil ;;(:line err-correct-line)
           :column nil ;;(:column err-correct-line)
           :err-msgs err-msgs}
     :str-expressions cbr
     :str-code (apply str (interpose "\n" cbr))}))

(defn cljtiles-eval [edn-code bindings]
  (let [the-err-msg (atom nil)
        cbr (code->break-str edn-code)
        erg (try (sci/eval-string cbr {:bindings bindings})
                 (catch js/Error e
                   (reset! the-err-msg {:message (.-message e)
                                        :line (:line (.-data e))
                                        :column (:column (.-data e))})
                   nil))]
    {:result {:expression erg}
     :err @the-err-msg
     :str-code cbr}))

(defn run-code [edn-code inspect-fn]
  (let [aug-edn-code (augment-code edn-code inspect-fn)
        new-println
        (fn [& x] (swap! state #(update % :stdout conj (apply str x))) nil)
        tex-inspect (fn [x] (swap! state #(update % :inspect conj x)) x)
        bindings2 (bindings new-println tex-inspect)
        _ (reset-state nil)
        erg (if inspect-fn
              (cljtiles-eval-one-by-one aug-edn-code bindings2)
              (cljtiles-eval aug-edn-code bindings2))]
    (swap! state merge
           (let [{:keys [result err str-code]} erg]
             {:sci-error (:message err)
              :sci-error-full err
              :result-raw (:expression result)
              :code str-code
              :edn-code aug-edn-code
              :edn-code-orig edn-code}))))

(defn insert-inspect [edn-code inspect-form]
    (if inspect-form
      (concat (take 3 edn-code)
              (conj (drop 3 edn-code) inspect-form))
      edn-code))

(defn get-edn-code [xml-str inspect-id inspect-fn]
  (let [edn-xml (sax/xml->clj xml-str)
        eci (edn->code/parse edn-xml {:id inspect-id :fun inspect-fn})
        inspect-fn-name (when inspect-fn (first (inspect-fn 0)))
        ifo (ca/inspect-froms eci inspect-fn-name)]
    (if (not-every? nil? ifo)
      (let [ec (edn->code/parse edn-xml {:id nil :fun nil})]
        (map insert-inspect ec ifo))
      eci)))

(defn ^:export startsci [{:keys [inspect-fn] :as context}]
  (let [xml-str (->> (.-mainWorkspace blockly)
                     (.workspaceToDom blockly/Xml)
                     (.domToPrettyText blockly/Xml))
        edn-code (get-edn-code xml-str
                               (when context (.-id (get context "block")))
                               inspect-fn)]
    (run-code edn-code inspect-fn)))

(defn open-modal []
  (swap! state assoc :modal-style-display "block"))

(defn modal-comp [_]
  (let [textarea-element (atom nil)
        parse
        (fn [e]
          (let [a (edn/read-string (str "[" e "]"))
                b (first a)]
            (if (and (map? b) (:code b))
              (:xml-code (generate-xml b))
              (map #(gb/rpg [] %) a))))
        run-parser
        (fn []
          (let [xml (parse (.-value @textarea-element))]
            (if (seq? xml)
              (run! (fn [c] (append-to-workspace c)) xml)
              (load-workspace xml))))
        close-modal
        (fn []
          (set! (.-value @textarea-element) "")
          (swap! state assoc :modal-style-display "none"))]
    (fn [{:keys [tutorial-no modal-style-display]}]
      [:div {:id "myModal", :class "modal"
             :style {:display modal-style-display}}
       [:div {:class "modal-content"}
        [:div
         [:textarea {:cols 80 :rows 10
                     :ref (fn [e] (reset! textarea-element e) (some-> e .focus))}]]
        [:button {:on-click #(do (run-parser) (close-modal))}
         "Insert"]
        " "
        (when-let [h (:hint (nth tutorials tutorial-no))]
          (let [i (atom 0)]
            [:button {:on-click (fn []
                                  (set! (.-value @textarea-element)
                                        (get h @i))
                                  (swap! i inc))}
             "Hint"]))
        [:button {:style {:float "right"} :on-click close-modal}
         "Cancel"]
        [:button {:style {:float "right"}
                  :on-click (fn []
                              (set! (.-value @textarea-element)
                                    "\"For more information go to:\"\n\"https://github.com/kloimhardt/clj-tiles\""))}
         "About"]]])))

(defn desc-button []
  [:button {:on-click #(reset-state nil)} "Clear screen"])

(defn get-inspect-form [edn-code]
  (ca/inspect-form edn-code workspace!/inspect-fn-sym))

(defn tutorials-comp [{:keys [run-button tutorial-no edn-code]}]
  [:div
   [:span
    [:select {:value (page->chapter tutorial-no)
              :on-change (fn [el]
                           (let [chap (gstring/toNumber
                                       (.. el -target -value))
                                 no (chapter->page chap)]
                             (goto-page! no)))}
     (map-indexed (fn [idx val] [:option {:key idx :value idx} val]) chapnames)]
    " "
    [:button {:on-click (tutorial-fu dec)} "<"]
    " "
    [:input {:read-only true :size (inc (* 2 (count (str (count tutorials)))))
             :value (str (inc tutorial-no) "/" (count tutorials))}]
    " "
    [:button {:on-click (tutorial-fu inc)} ">"]
    " "
    (when run-button
      (if (get-inspect-form edn-code)
        [desc-button]
        [:button {:on-click #(startsci nil)} "Run"]))]])

(defn my-str [x]
  (if (nil? x) "nil" (str x)))

(defn my-str-brk [e]
  (if (seq? e)
    (part-str output-width (apply str (interpose " " (map my-str e))))
    (part-str output-width (my-str e))))

(defn modify-error [msg]
  (if (= (subs msg 0 40)
         "Parameter declaration should be a vector")
    (str "Wrong Parameter declaration" (subs msg 40))
    msg))

(defn error-comp-inspect [{:keys [sci-error-full code]} ifo]
  (let [flex50 {:style {:flex "50%"}}]
    [:div {:style {:display "flex"}}
     [:div flex50
      [:h3 "Code interpretation result"]
      [:pre (my-str-brk (str "The expression " ifo " could not be displayed due to one of the following reasons:"))]
      (map-indexed (fn [idx msg] ^{:key idx} [:pre (my-str-brk (str (inc idx) ". " (modify-error msg)))])
                   (:err-msgs sci-error-full) )]
     [:div flex50
      [:h3 "Code"]
      [:pre code]]]))

(defn error-comp [{:keys [sci-error-full sci-error code]}]
  (let [flex50 {:style {:flex "50%"}}]
    [:div {:style {:display "flex"}}
     [:div flex50
      [:h3 "Code interpretation result"]
      [:pre (my-str-brk (modify-error sci-error))]
      [:pre (str "line " (:line sci-error-full) " column " (:column sci-error-full))]]
     [:div flex50
      [:h3 "Code"]
      [:pre code]]]))

(defn result-comp [{:keys [result-raw edn-code edn-code-orig code]}]
  (if (= edn-code edn-code-orig :showcode) ;;never true, remove :showcode to supress code display.
    [:pre (my-str result-raw)]
    (let [flex50 {:style {:flex "50%"}}]
      [:div {:style {:display "flex"}}
       [:div flex50
        [:h3 "Result"]
        [:pre (my-str-brk result-raw)]]
       [:div flex50
        [:h3 "Code"]
        [:pre code]]])))

(defn output-comp [{:keys [edn-code tutorial-no inspect sci-error stdout
                           desc result-raw edn-code-orig code] :as the-state}]
  (if-let [ifo (get-inspect-form edn-code)]
    (let [tut (nth tutorials tutorial-no)]
      [:<>
       (cond
         (seq inspect)
         [:<>
          (map-indexed (fn [idx v]
                         ^{:key idx} [:<>
                                      [tex-comp (sicm/kind? v)]
                                      [:hr]])
                       inspect)
          (when-let [msg-fn (:message-fn tut)]
            [tex-comp (msg-fn the-state ifo goto-lable-page!)])]
         sci-error
         [:<>
          (when-let [error-msg-fn (:error-message-fn tut)]
            [:p (error-msg-fn the-state ifo (:message-fn tut))])
          [error-comp-inspect the-state (last ifo)]]
         (= (last ifo) :start-interactive)
         [tex-comp ((:message-fn tut) the-state ifo goto-lable-page!)]
         :else (str "Expression " (last ifo) " was never called.")
         )])
    [:<>
     (map-indexed (fn [idx v]
                    ^{:key idx} [:pre v])
                  stdout)
     (cond
       sci-error
       [:<>
        [error-comp the-state]
        (when desc [desc-button])]
       code
       [:<>
        (if (start-with-div? (last edn-code-orig))
          [result-raw]
          [result-comp the-state])
        (when desc [desc-button])]
       desc
       [:div {:style {:column-count 2}}
        [tex-comp desc]]
       )
     ]))

(defn error-boundary [comp]
  (let [error (rc/atom nil)]
    (rc/create-class
      {:component-did-catch (fn [this e info] nil)
       :get-derived-state-from-error (fn [e]
                                       (reset! error e)
                                       #js {})
       :reagent-render (fn [comp]
                         (if @error
                           (do
                             (reset-state nil)
                             (swap! state assoc :stdout ["Something went wrong rendering the result"])
                             (reset! error nil)
                             nil)
                           comp))})))

(defn theview []
  [:div
   [modal-comp @state]
   [tutorials-comp @state]
   [error-boundary
    [output-comp @state]]])


(defn ^{:dev/after-load true} render []
  ;;((tutorial-fu identity)) ;;load currenet workspace new
  (rd/render [theview] (gdom/getElement "out")))

(defn ^{:export true} output []
  (workspace!/init startsci open-modal)
  (goto-page! (dec 1))
  (when-let [p (some-> (not-empty (.. js/window -location -search))
                       js/URLSearchParams.
                       (.get "page"))]
    (if (= p "freeparticle")
      (do
        (goto-lable-page! :free-particle)
        (swap! state assoc :run-button false)
        (swap! state assoc :edn-code (list workspace!/inspect-fn-sym :start-interactive)))
      (-> p
          js/parseInt
          dec
          goto-page!)))
  (render))
