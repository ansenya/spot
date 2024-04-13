import {configureStore} from "@reduxjs/toolkit";
import {serverApi} from "./api/serverApi";
import {setupListeners} from "@reduxjs/toolkit/query";
import {authReducer} from "./slices/authSlice";
import { focusReducer } from "./slices/focusSlice";
import { commentReducer } from "./slices/commentPostSlice";
import { deletedVideosReducer } from "./slices/DeletedVideoSlice";


export const store = configureStore({
    reducer: {
        [serverApi.reducerPath]: serverApi.reducer,
        auth: authReducer,
        focus: focusReducer,
        comment: commentReducer,
        deletedVideos: deletedVideosReducer,
    },
    middleware: (getDefaultMiddleware) => getDefaultMiddleware().concat(serverApi.middleware)
})

setupListeners(store.dispatch)
export type RootState = ReturnType<typeof store.getState>

