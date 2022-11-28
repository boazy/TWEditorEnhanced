$TW_EDITOR_ENHANCED_OPT_IN="C:\Program Files (x86)\Steam\steamapps\common\The Witcher Enhanced Edition"
$DEFAULT_GAME_LOC = Read-Host "Is The Witcher Enhanced Edition installed at the default location for Steam on disk C:? [y/n]:"

if($DEFAULT_GAME_LOC -eq "n"){
	$TW_EDITOR_ENHANCED_OPT_IN = Read-Host "Enter the full path of the base game directory, without quotes:"
}

$Env:TW_EDITOR_ENHANCED_OPTS="`"-DTW.install.path="+$TW_EDITOR_ENHANCED_OPT_IN+"`""

# Write-Host "TW_EDITOR_ENHANCED_OPTS is now: " $Env:TW_EDITOR_ENHANCED_OPTS

start .\bin\TWEditorEnhanced.bat -Wait -NoNewWindow

