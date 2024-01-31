#_"SPDX-License-Identifier: GPL-3.0"

(ns cljtiles.blockly
  (:require
   ["blockly" :as blockly]

   [goog.dom :as gdom]
   ))

(def dev true)
(if dev
  (def menu false)
  (def menu false))

(def inspect-fn-sym 'tex-inspect)

(defn init [startfun open-modal]
  (js/initblocks blockly)
  (.inject blockly
           "blocklyDiv"
           (clj->js (merge {:scrollbars true
                            :media "/blockly/media/"}
                           (when menu {:toolbox (gdom/getElement "toolbox")}))))
  (run! #(.. blockly -ContextMenuRegistry -registry (register %))
        [#js {:displayText "Parser"
              :preconditionFn (fn [_scope] "enabled")
              :callback (fn [_scope] (open-modal))
              :scopeType (.. blockly -ContextMenuRegistry -ScopeType -WORKSPACE)
              :id "parser"
              :weight 0}
         #js {:displayText "Run Workspace"
              :preconditionFn (fn [_scope] "enabled")
              :callback (fn [_scope] (startfun nil))
              :scopeType (.. blockly -ContextMenuRegistry -ScopeType -WORKSPACE)
              :id "run_workspace"
              :weight 0}
         #js {:displayText "Inspect"
              :preconditionFn (fn [_scope] "enabled")
              :callback (fn [scope] (startfun (assoc (js->clj scope) :inspect-fn #(list inspect-fn-sym %))))
              :scopeType (.. blockly -ContextMenuRegistry -ScopeType -BLOCK)
              :id "tex-inspect"
              :weight 0}]))
