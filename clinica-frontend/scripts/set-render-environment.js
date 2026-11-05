const fs = require('fs');
const path = require('path');

const authApiBaseUrl = process.env.AUTH_API_BASE_URL;
const clinicApiBaseUrl = process.env.CLINIC_API_BASE_URL;
const turnstileSiteKey = process.env.TURNSTILE_SITE_KEY;

if (!authApiBaseUrl || !clinicApiBaseUrl || !turnstileSiteKey) {
  throw new Error('AUTH_API_BASE_URL, CLINIC_API_BASE_URL and TURNSTILE_SITE_KEY are required');
}

const normalize = value => value.replace(/\/$/, '');
const content = `export const environment = {
  production: true,
  authApiBaseUrl: ${JSON.stringify(normalize(authApiBaseUrl))},
  clinicApiBaseUrl: ${JSON.stringify(normalize(clinicApiBaseUrl))},
  turnstileSiteKey: ${JSON.stringify(turnstileSiteKey)}
};
`;

fs.writeFileSync(
  path.join(__dirname, '..', 'src', 'environments', 'environment.production.ts'),
  content,
  'utf8'
);
