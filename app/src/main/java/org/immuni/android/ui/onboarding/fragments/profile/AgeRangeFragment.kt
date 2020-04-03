package org.immuni.android.ui.onboarding.fragments.profile

import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import androidx.core.widget.doOnTextChanged
import org.immuni.android.R
import org.immuni.android.ui.onboarding.OnboardingUserInfo
import com.bendingspoons.base.extensions.hideKeyboard
import com.bendingspoons.base.extensions.showKeyboard
import com.bendingspoons.base.utils.ScreenUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.onboarding_age_range_fragment.*
import org.immuni.android.models.AgeGroup
import org.immuni.android.models.AgeGroup.*
import kotlin.math.pow

class AgeRangeFragment : ProfileContentFragment(R.layout.onboarding_age_range_fragment),
    CompoundButton.OnCheckedChangeListener {
    override val nextButton: View
        get() = next

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // on scrolling the top mask hide/show
        scrollView.setOnScrollChangeListener { v: View?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            updateTopMask(scrollY)
        }

        updateTopMask(scrollView.scrollY)

        nextButton.setOnClickListener(null) // override the default behaviour
        nextButton.setOnClickListener {

            if (lastRadioSelected == R.id.age_range_0_17) {
                MaterialAlertDialogBuilder(context)
                    .setTitle(getString(R.string.onboarding_age_more_than_18_title))
                    .setMessage(getString(R.string.onboarding_age_more_than_18_message))
                    .setPositiveButton(getString(R.string.ok)) { d, _ -> d.dismiss() }
                    .setOnCancelListener { }
                    .show()
            } else {
                viewModel.onNextTap()
            }
        }

        back.setOnClickListener {
            viewModel.onPrevTap()
        }

        age_range_0_17.setOnCheckedChangeListener(this)
        age_range_18_35.setOnCheckedChangeListener(this)
        age_range_36_45.setOnCheckedChangeListener(this)
        age_range_46_55.setOnCheckedChangeListener(this)
        age_range_56_65.setOnCheckedChangeListener(this)
        age_range_66_75.setOnCheckedChangeListener(this)
        age_range_75.setOnCheckedChangeListener(this)
    }

    private fun updateTopMask(scrollY: Int) {
        val dp = ScreenUtils.convertDpToPixels(requireContext(), 8).toFloat()
        val elevation = resources.getDimension(R.dimen.top_scroll_mask_elevation)
        topMask.alpha = 1f//0f + scrollY/dp
        topMask.elevation = (elevation * (0f + scrollY/dp))
    }

    override fun onUserInfoUpdate(userInfo: OnboardingUserInfo) {
        disableTriggeringEvent = true
        updateUI(userInfo.ageGroup)
        validate(false)
        disableTriggeringEvent = false
    }

    private fun validate(updateModel: Boolean = true): Boolean {
        var valid = lastRadioSelected != -1
        nextButton.isEnabled = valid
        if (valid && updateModel) updateModel(lastRadioSelected)
        return valid
    }

    private fun updateModel(id: Int) {
        viewModel.userInfo()?.let {
            viewModel.updateUserInfo(
                it.copy(
                    ageGroup = when (id) {
                        R.id.age_range_0_17 -> ZERO_SEVENTEEN
                        R.id.age_range_18_35 -> EIGHTEEN_THIRTYFIVE
                        R.id.age_range_36_45 -> THRITYSIX_FORTYFIVE
                        R.id.age_range_46_55 -> FORTYSIX_FIFTYFIVE
                        R.id.age_range_56_65 -> FIFTYSIX_SIXTYFIVE
                        R.id.age_range_66_75 -> SIXTYSIX_SEVENTYFIVE
                        else -> MORE_THAN_SEVENTYFIVE
                    }
                )
            )
        }
    }

    private fun updateUI(ageGroup: AgeGroup?) {
        radioGroup.clearCheck()

        age_range_0_17.isChecked = false
        age_range_18_35.isChecked = false
        age_range_36_45.isChecked = false
        age_range_46_55.isChecked = false
        age_range_56_65.isChecked = false
        age_range_66_75.isChecked = false
        age_range_75.isChecked = false

        when (ageGroup) {
            ZERO_SEVENTEEN -> {
                age_range_0_17.isChecked = true
                lastRadioSelected = R.id.age_range_0_17
            }
            EIGHTEEN_THIRTYFIVE -> {
                age_range_18_35.isChecked = true
                lastRadioSelected = R.id.age_range_18_35
            }
            THRITYSIX_FORTYFIVE -> {
                age_range_36_45.isChecked = true
                lastRadioSelected = R.id.age_range_36_45
            }
            FORTYSIX_FIFTYFIVE -> {
                age_range_46_55.isChecked = true
                lastRadioSelected = R.id.age_range_46_55
            }
            FIFTYSIX_SIXTYFIVE -> {
                age_range_56_65.isChecked = true
                lastRadioSelected = R.id.age_range_56_65
            }
            SIXTYSIX_SEVENTYFIVE -> {
                age_range_66_75.isChecked = true
                lastRadioSelected = R.id.age_range_66_75
            }
            MORE_THAN_SEVENTYFIVE -> {
                age_range_75.isChecked = true
                lastRadioSelected = R.id.age_range_75
            }
            null -> {
            }
        }
    }

    var lastRadioSelected = -1
    var disableTriggeringEvent = false
    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        if (isChecked) lastRadioSelected = buttonView?.id ?: -1
        if (!disableTriggeringEvent) validate(true)
    }
}
