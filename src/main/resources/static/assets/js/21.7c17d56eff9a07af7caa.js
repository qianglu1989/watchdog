webpackJsonp([21],{"249w":function(e,t){},TTwy:function(e,t,r){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var n=r("mtWM"),a=r.n(n),i={name:"register",data:function(){return{registerAdminUrl:""}},mounted:function(){var e=this;a.a.defaults.headers.common.project=window.localStorage.getItem("pjValue"),a.a.defaults.headers.common.profile=window.localStorage.getItem("prValue"),a.a.get("/appConfig/getMonitorHosts",{params:{name:"redefine.monitor.register"}}).then(function(t){e.registerAdminUrl=t.data})}},o={render:function(){var e=this.$createElement,t=this._self._c||e;return t("div",{staticClass:"register-admin"},[t("iframe",{attrs:{src:this.registerAdminUrl,frameborder:"0"}})])},staticRenderFns:[]};var s=r("VU/8")(i,o,!1,function(e){r("249w")},null,null);t.default=s.exports}});
//# sourceMappingURL=21.7c17d56eff9a07af7caa.js.map