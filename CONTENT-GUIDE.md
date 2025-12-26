# Content Update Guide for Sales Team

## How to Update Website Content

All website text content is stored in `content.json`. You can update any text without touching HTML files.

### Quick Start

1. Open `content.json` in any text editor
2. Find the section you want to update
3. Change the text between the quotes
4. Save the file
5. Refresh the website to see changes

### Content Structure

#### Navigation
- `brandName`: Company name in the header
- `links`: Array of navigation menu items
  - `text`: Menu item text
  - `href`: Link destination (e.g., "#about")
  - `isCta`: Set to `true` for special styling (like LOGIN button)

#### Hero Section
- `title`: Main heading (use `\n` for line breaks)
- `highlight`: Subheading text
- `description`: Paragraph text below the heading
- `ctaText`: Button text
- `ctaLink`: Button link destination

#### Why Choose Us Section
- `title`: Section heading
- `cards`: Array of feature cards
  - `title`: Card heading
  - `description`: Card text

#### About Section
- `title`: "WHO WE ARE" heading
- `description`: Paragraph text
- `mission`: Mission card content
- `vision`: Vision card content

#### Services Section
- `title`: "WHAT WE OFFER" heading
- `cards`: Array of service cards
  - `title`: Service name
  - `description`: Service description

#### Footer
- `copyright`: Copyright text

### Important Notes

⚠️ **DO NOT:**
- Delete any section or property names
- Remove quotes around text
- Add extra commas at the end of arrays
- Change the structure/formatting

✅ **DO:**
- Update text between quotes
- Keep the same structure
- Use `\n` for line breaks in titles
- Save as valid JSON format

### Example: Updating Hero Text

**Before:**
```json
"hero": {
  "title": "CYBER\nSECURITY",
  "highlight": "PROTECTING YOUR DIGITAL WORLD",
  "description": "Lorem ipsum..."
}
```

**After:**
```json
"hero": {
  "title": "CYBER\nSECURITY",
  "highlight": "YOUR TRUSTED SECURITY PARTNER",
  "description": "We provide world-class cybersecurity solutions..."
}
```

### Need Help?

If you make a mistake and the website breaks:
1. Check for missing quotes or commas
2. Use an online JSON validator (like jsonlint.com)
3. Contact the development team

