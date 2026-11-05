import { Injectable } from '@angular/core';

export interface TurnstileRenderOptions {
  sitekey: string;
  action: string;
  appearance: 'always';
  theme: 'auto';
  callback: (token: string) => void;
  'expired-callback': () => void;
  'error-callback': () => void;
}

export interface TurnstileApi {
  render(container: HTMLElement, options: TurnstileRenderOptions): string;
  reset(widgetId: string): void;
  remove(widgetId: string): void;
}

declare global {
  interface Window {
    turnstile?: TurnstileApi;
  }
}

const SCRIPT_ID = 'cloudflare-turnstile-script';
let turnstilePromise: Promise<TurnstileApi> | undefined;

@Injectable({ providedIn: 'root' })
export class TurnstileService {
  load(): Promise<TurnstileApi> {
    if (window.turnstile) {
      return Promise.resolve(window.turnstile);
    }
    if (turnstilePromise) {
      return turnstilePromise;
    }

    turnstilePromise = new Promise<TurnstileApi>((resolve, reject) => {
      const script = document.createElement('script');
      script.id = SCRIPT_ID;
      script.src = 'https://challenges.cloudflare.com/turnstile/v0/api.js?render=explicit';
      script.async = true;
      script.defer = true;
      script.onload = () => window.turnstile
        ? resolve(window.turnstile)
        : reject(new Error('Turnstile API unavailable'));
      script.onerror = () => {
        script.remove();
        turnstilePromise = undefined;
        reject(new Error('Turnstile script failed to load'));
      };
      document.head.appendChild(script);
    });

    return turnstilePromise;
  }
}
