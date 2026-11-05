import { TestBed } from '@angular/core/testing';
import { TurnstileApi, TurnstileService } from './turnstile.service';

describe('TurnstileService', () => {
  it('loads the explicit Cloudflare script only once', async () => {
    const service = TestBed.inject(TurnstileService);
    const first = service.load();
    const second = service.load();
    const scripts = document.querySelectorAll('#cloudflare-turnstile-script');
    expect(scripts.length).toBe(1);
    expect((scripts[0] as HTMLScriptElement).src).toContain('api.js?render=explicit');

    const api = jasmine.createSpyObj<TurnstileApi>('TurnstileApi', ['render', 'reset', 'remove']);
    window.turnstile = api;
    (scripts[0] as HTMLScriptElement).onload?.(new Event('load'));

    await expectAsync(first).toBeResolvedTo(api);
    await expectAsync(second).toBeResolvedTo(api);
    delete window.turnstile;
    scripts[0].remove();
  });
});
