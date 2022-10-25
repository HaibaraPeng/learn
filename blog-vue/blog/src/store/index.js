import Vue from "vue";
import Vuex from "vuex";
import createPersistedState from "vuex-persistedstate";

Vue.use(Vuex)

export default new Vuex.Store({
    state: {
        searchFlag: false,
        blogInfo: {}
    },
    mutations: {
        checkBlogInfo(state, blogInfo) {
            state.blogInfo = blogInfo;
        }
    },
    actions: {},
    modules: {},
    plugins: [
        createPersistedState({
            storage: window.sessionStorage
        })
    ]
})