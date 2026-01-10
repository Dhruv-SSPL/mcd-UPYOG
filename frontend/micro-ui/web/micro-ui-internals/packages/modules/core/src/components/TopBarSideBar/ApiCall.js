import { UserService } from '@nudmcdgnpm/digit-ui-libraries/src/services/elements/User';
import axios from 'axios';

export const logoutEGF = async () => {
    try {
        const user = UserService.getUser();
        if (!user || !user.access_token) {
            throw new Error("User or access token is missing");
        }
        const payload = {
            RequestInfo: {
                apiId: null,
                ver: null,
                ts: Math.floor(new Date().getTime() / 1000),
                action: null,
                did: null,
                key: null,
                msgId: null,
                authToken: user.access_token,
                correlationId: null,
                userInfo: null,
            }
        };

        const baseProxy = process.env.REACT_APP_PROXY_API;
        if (!baseProxy) {
            throw new Error("Base proxy API URL is not defined");
        }

        const parsedURL = new URL(baseProxy);
        const domain = parsedURL.hostname;
        const protocol = parsedURL.protocol;
        const clearTokenURL = `${protocol}//${domain}/services/EGF/rest/logout`;

        const response = await axios({
            method: 'POST',
            url: clearTokenURL,
            data: payload,
            headers: {
                'Content-Type': 'application/json',
            },
        });
        return response.data;
    } catch (error) {
        return { error: error.message };
    }
};

export const logoutV2 = async () => {
    const userType = UserService.getType();
    try {
        await logoutEGF();
    } catch (e) {
        console.error("Error during logoutEGF:", e);
    } finally {
        window.localStorage.clear();
        window.sessionStorage.clear();

        if (userType === "citizen") {
            window.location.replace("/digit-ui/citizen");
        } else {
            window.location.replace("/digit-ui/employee/user/language-selection");
        }
    }
};
