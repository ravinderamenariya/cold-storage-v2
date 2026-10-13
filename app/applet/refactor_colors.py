import os
import re

mappings = {
    'VxSurfaceSubtle': 'VxTheme.surfaceSubtle',
    'VxSurfaceBlue': 'VxTheme.surfaceBlue',
    'VxSurfaceBlueAccent': 'VxTheme.surfaceBlueAccent',
    'VxSurfaceGreen': 'VxTheme.surfaceGreen',
    'VxSurfaceWarning': 'VxTheme.surfaceWarning',
    'VxSurfaceDanger': 'VxTheme.surfaceDanger',
    'VxSurfacePurple': 'VxTheme.surfacePurple',
    'VxSolarSurface': 'VxTheme.solarSurface',
    'VxBorder': 'VxTheme.border',
    'VxBorderSubtle': 'VxTheme.borderSubtle',
    'VxBorderBlue': 'VxTheme.borderBlue',
    'VxBorderPurple': 'VxTheme.borderPurple',
    'VxBorderGreen': 'VxTheme.borderGreen',
    'VxBorderWarning': 'VxTheme.borderWarning',
    'VxBorderDanger': 'VxTheme.borderDanger',
    'VxTextPrimary': 'VxTheme.textPrimary',
    'VxTextSecondary': 'VxTheme.textSecondary',
    'VxTextMuted': 'VxTheme.textMuted',
    'VxTextInverse': 'VxTheme.textInverse',
    'VxNavy': 'VxTheme.navy',
    'VxNavyDark': 'VxTheme.navyDark',
    'VxNavySurface': 'VxTheme.navySurface',
    'VxNavyLight': 'VxTheme.navyLight',
    'VxPrimaryAccent': 'VxTheme.primaryAccent',
    'VxPrimaryBlue': 'VxTheme.primaryBlue',
    'VxDeepBlue': 'VxTheme.deepBlue',
    'VxLightBlue': 'VxTheme.lightBlue',
    'VxCyanAccent': 'VxTheme.cyanAccent',
    'VxSteelBlue': 'VxTheme.lightBlue',
    'VxSlateBlue': 'VxTheme.primaryAccent',
    'VxPurple': 'VxTheme.purple',
    'VxSuccess': 'VxTheme.success',
    'VxEmerald': 'VxTheme.emerald',
    'VxEmeraldDark': 'VxTheme.emeraldDark',
    'VxEmeraldAccent': 'VxTheme.emerald',
    'VxWarningAmber': 'VxTheme.warningAmber',
    'VxWarningDark': 'VxTheme.warningDark',
    'VxError': 'VxTheme.errorRed',
    'VxDangerRed': 'VxTheme.errorRed',
    'VxDangerDark': 'VxTheme.dangerDark',
    'VxSolarGold': 'VxTheme.solarGold',
}

ui_dir = 'app/src/main/java/com/example/ui'

for root, dirs, files in os.walk(ui_dir):
    for file in files:
        if file.endswith('.kt'):
            path = os.path.join(root, file)
            with open(path, 'r', encoding='utf-8') as f:
                content = f.read()
            
            modified = content
            for old, new in mappings.items():
                modified = re.sub(r'\b' + old + r'\b', new, modified)
            
            if modified != content:
                with open(path, 'w', encoding='utf-8') as f:
                    f.write(modified)
                print(f"Refactored {path}")

print("Refactoring complete.")
