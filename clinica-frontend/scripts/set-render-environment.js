const fs = require('fs');
const path = require('path');

const authApiBaseUrl = process.env.AUTH_API_BASE_URL;
const clinicApiBaseUrl = process.env.CLINIC_API_BASE_URL;

if (!authApiBaseUrl || !clinicApiBaseUrl) {
  throw new Error('AUTH_API_BASE_URL and CLINIC_API_BASE_URL are required');
}

const normalize = value => value.replace(/\/$/, '');
const content = `export const environment = {
  production: true,
  authApiBaseUrl: ${JSON.stringify(normalize(authApiBaseUrl))},
  clinicApiBaseUrl: ${JSON.stringify(normalize(clinicApiBaseUrl))}
};
`;

fs.writeFileSync(
  path.join(__dirname, '..', 'src', 'environments', 'environment.production.ts'),
  content,
  'utf8'
);
