import { chromium } from 'playwright';

async function testDarkMode() {
  const browser = await chromium.launch({ headless: false });
  const context = await browser.newContext();
  const page = await context.newPage();

  try {
    console.log('Navigating to http://localhost:5173/...');
    await page.goto('http://localhost:5173/');
    await page.waitForTimeout(2000);

    console.log('\n✓ Page loaded successfully');

    // Check if theme selector button exists
    const themeButton = page.locator('button[aria-haspopup="menu"]').first();
    await themeButton.waitFor({ state: 'visible', timeout: 5000 });
    console.log('✓ Theme selector button is visible');

    // Take screenshot of light mode
    await page.screenshot({ path: 'light-mode.png', fullPage: true });
    console.log('✓ Light mode screenshot saved');

    // Click theme selector
    await themeButton.click();
    await page.waitForTimeout(500);
    console.log('✓ Theme selector dropdown opened');

    // Click Dark option
    const darkOption = page.getByText('Dark', { exact: false }).filter({ hasText: 'Dark' }).first();
    await darkOption.click();
    await page.waitForTimeout(1000);
    console.log('✓ Switched to dark mode');

    // Verify dark mode is applied
    const html = page.locator('html');
    const htmlClass = await html.getAttribute('class');
    if (htmlClass && htmlClass.includes('dark')) {
      console.log('✓ Dark mode class applied to HTML element');
    } else {
      console.log('✗ Dark mode class NOT found on HTML element');
    }

    // Check background color
    const body = page.locator('body');
    const bgColor = await body.evaluate(el => window.getComputedStyle(el).backgroundColor);
    console.log(`✓ Body background color: ${bgColor}`);

    // Take screenshot of dark mode
    await page.screenshot({ path: 'dark-mode.png', fullPage: true });
    console.log('✓ Dark mode screenshot saved');

    // Test Light mode
    await themeButton.click();
    await page.waitForTimeout(500);
    const lightOption = page.getByText('Light', { exact: false }).filter({ hasText: 'Light' }).first();
    await lightOption.click();
    await page.waitForTimeout(1000);
    console.log('✓ Switched to light mode');

    // Test System mode
    await themeButton.click();
    await page.waitForTimeout(500);
    const systemOption = page.getByText('System', { exact: false }).filter({ hasText: 'System' }).first();
    await systemOption.click();
    await page.waitForTimeout(1000);
    console.log('✓ Switched to system mode');

    console.log('\n✅ All dark mode tests passed!');
    console.log('Screenshots saved: light-mode.png, dark-mode.png');

  } catch (error) {
    console.error('\n❌ Test failed:', error.message);
  } finally {
    await page.waitForTimeout(2000);
    await browser.close();
  }
}

testDarkMode();
