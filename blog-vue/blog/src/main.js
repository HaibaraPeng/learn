import Vue from "vue";
import App from "./App"
import config from "./assets/js/config"
import axios from "axios";
import VueAxios from "vue-axios";
import vuetify from "./plugins/vuetify";
import store from "./store";

Vue.prototype.config = config;
Vue.config.productionTip = false;
Vue.use(VueAxios, axios);

new Vue({
    store,
    vuetify,
    render: h => h(App)
}).$mount("#app")
