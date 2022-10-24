import Vue from "vue";
import App from "./App"
import config from "./assets/js/config"
import axios from "axios";
import VueAxios from "vue-axios";
import vuetify from "./plugins/vuetify";

Vue.prototype.config = config;
Vue.config.productionTip = false;
Vue.use(VueAxios, axios);

new Vue({
    vuetify,
    render: h => h(App)
}).$mount("#app")
